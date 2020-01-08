package com.spike.kafkasteam;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;

import java.util.Properties;
import java.util.concurrent.CountDownLatch;


public class KtableGenerator {

    private static Serde<MyTransaction> transactionSerde = Serdes.serdeFrom(new TransactionSerializer(), new TransactionDeserializer());
    private static Serde<ResolvedTransaction> resolvedTransactionSerde = Serdes.serdeFrom(new ResolvedTransactionSerializer(), new ResolvedTransactionDeserializer());
    private static Serde<BankMaster> bankMasterSerde = Serdes.serdeFrom(new BankMasterSerializer(), new BankMasterDeserializer());
    private static Serde<OutletTransaction> outletTransactionSerde = Serdes.serdeFrom(new OutletTransactionSerializer(), new OutletTransactionDeserializer());
    private static Serde<ResolvedTransactionOutlet> resolvedTransactionOutletSerde = Serdes.serdeFrom(new ResolvedTransactionOutletSerializer(), new ResolvedTransactionOutletDeserializer());
    private static Serde<String> stringSerde = Serdes.serdeFrom(String.class);

    static Properties getStreamsConfig() {
        final Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "streams-transactions");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 0);
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());

        // setting offset reset to earliest so that we can re-run the demo code with the same pre-loaded data
        // Note: To re-run the demo, you need to use the offset reset tool:
        // https://cwiki.apache.org/confluence/display/KAFKA/Kafka+Streams+Application+Reset+Tool
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return props;
    }


    public static void main(final String[] args) {

        final StreamsBuilder builder = new StreamsBuilder();

        generateTransactionAndBankDetailsData(builder);

        generateTransactionAndOutlookData(builder);


        final KafkaStreams streams = new KafkaStreams(builder.build(), getStreamsConfig());
        final CountDownLatch latch = new CountDownLatch(1);

        Runtime.getRuntime().addShutdownHook(new Thread("streams-transactions-shutdown-hook") {
            @Override
            public void run() {
                streams.close();
                latch.countDown();
            }
        });

        try {
            streams.start();
            latch.await();
        } catch (final Throwable e) {
            System.exit(1);
        }
        System.exit(0);
    }

    private static void generateTransactionAndBankDetailsData(StreamsBuilder builder) {
        KStream<String, MyTransaction> transactions = builder.stream("transaction_master",
                Consumed.with(stringSerde, transactionSerde));

        KStream<String, MyTransaction> transactions_mapped =
                transactions.map((key, value) -> KeyValue.pair(value.transactionId, value));

        final GlobalKTable<String, BankMaster> bankMasterGlobalKTable = builder.globalTable("bankmaster",
                Consumed.with(Serdes.String(), bankMasterSerde));


        KStream<String, ResolvedTransaction> resolvedTransactions =
                transactions_mapped.
                        leftJoin(bankMasterGlobalKTable, (key, value) -> value.ifscCode,
                                (transactionData, bankData) ->
                                {
                                    if (bankData != null)
                                        return new ResolvedTransaction(bankData.ifscCode, transactionData.transactionId,
                                                transactionData.customerId,
                                                transactionData.description, bankData.branchName);
                                    return new ResolvedTransaction(transactionData.ifscCode, transactionData.transactionId, transactionData.customerId, transactionData.description, "");
                                }
                        );

        resolvedTransactions.to("resolvedtransactions", Produced.with(Serdes.String(), resolvedTransactionSerde));
    }

    private static void generateTransactionAndOutlookData(StreamsBuilder builder) {
        KTable<String, ResolvedTransaction> transactionbankKTable = builder
                .table("resolvedtransactions", Consumed.with(Serdes.String(), resolvedTransactionSerde));

        KTable<String, OutletTransaction> outletKTable = builder
                .table("transactionoutlets", Consumed.with(Serdes.String(), outletTransactionSerde));

        KTable<String, ResolvedTransactionOutlet> resolvedOutletTransactions = transactionbankKTable.leftJoin(outletKTable,
                (transaction, outlet) -> {
                    if (outlet != null)
                        return new ResolvedTransactionOutlet(transaction, outlet.outletName);
                    return new ResolvedTransactionOutlet(transaction, "");
                }
        );

        resolvedOutletTransactions.toStream().to("resolvedoutlettransactions", Produced.with(Serdes.String(), resolvedTransactionOutletSerde));
    }
}



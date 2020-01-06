package com.spike.kafkasteam;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;

import java.util.Properties;
import java.util.concurrent.CountDownLatch;


public class KtableGenerator {



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
        Serde<MyTransaction> transactionSerde =  Serdes.serdeFrom(new TransactionSerializer(), new TransactionDeserializer());
        Serde<ResolvedTransaction> resolvedTransactionSerde = Serdes.serdeFrom(new ResolvedTransactionSerializer(), new ResolvedTransactionDeserializer());
        Serde<BankMaster> bankMasterSerde =  Serdes.serdeFrom(new BankMasterSerializer(), new BankMasterDeserializer());;
        Serde<String> stringSerde = Serdes.serdeFrom(String.class);


        KStream<String, Long> left = null;
        KStream<String, Double> right = null;


        KStream<String, MyTransaction> transactions_3 = builder.stream("transactions_5",
                Consumed.with(stringSerde, transactionSerde));

        System.out.print("Completed the initial parsing");




        final GlobalKTable<String, BankMaster> bankMasterGlobalKTable = builder.globalTable("bank_master_4",
                Consumed.with(Serdes.String(), bankMasterSerde));


        System.out.print("Completed the second parsing");

        KStream<String, ResolvedTransaction> resolvedTransactions =
                transactions_3.
                leftJoin(bankMasterGlobalKTable, (left1, right1) -> left1,
                        (left2, right2) ->
                                new ResolvedTransaction(right2.ifscCode, left2.transactionId, right2.branchName)
                        );

//        final KStream<String, ResolvedTransaction> resolvedTransactions = transactions.map(
//                (key, transaction) -> KeyValue.pair(transaction.IfscCode, transaction)).
//                join(bankMasterKTable,(transaction, bankMaster, value) -> "" , (transaction, bankmaster) -> );

//        KStream<String, ResolvedTransaction> resolvedTransactions = transactions.join(bankMasterKTable,
//                (MyTransaction transaction, BankMaster bankMaster) -> new ResolvedTransaction(transaction.ifscCode, transaction.transactionId, bankMaster.branchName), /* ValueJoiner */
//                Serdes.String(), /* key */
//                transactionSerde    /* left value */
//        );

        resolvedTransactions.to("transaction_output", Produced.with(Serdes.String(), resolvedTransactionSerde));




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
}



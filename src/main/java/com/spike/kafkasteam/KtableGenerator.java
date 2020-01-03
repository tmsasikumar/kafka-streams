package com.spike.kafkasteam;

import com.sun.tools.javac.util.Pair;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.clients.consumer.ConsumerConfig;

import java.time.Duration;
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
        Serde<Transaction> transactionSerde = null;
        Serde<ResolvedTransaction> resolvedTransactionSerde = null;
        Serde<BankMaster> bankMasterSerde = null;
        Serde<String> stringSerde = null;


        KStream<String, Long> left = null;
        KStream<String, Double> right = null;

        KStream<String, String> joined = left.join(right,
                (leftValue, rightValue) -> "left=" + leftValue + ", right=" + rightValue, /* ValueJoiner */
                JoinWindows.of(Duration.ofMinutes(5)),
                Joined.with(
                        Serdes.String(), /* key */
                        Serdes.Long(),   /* left value */
                        Serdes.Double())  /* right value */
        );

        final KStream<String, Transaction> transactions = builder.stream("transactions",
                Consumed.with(stringSerde, transactionSerde));



        final GlobalKTable<String, BankMaster> bankMasterKTable = builder.globalTable("bank_master",
                Consumed.with(Serdes.String(), bankMasterSerde));



//        final KStream<String, ResolvedTransaction> resolvedTransactions = transactions.map(
//                (key, transaction) -> KeyValue.pair(transaction.IfscCode, transaction)).
//                join(bankMasterKTable,(transaction, bankMaster, value) -> "" , (transaction, bankmaster) -> );
//


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



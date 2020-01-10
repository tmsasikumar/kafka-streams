package com.spike.kafkasteam.deserializers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spike.kafkasteam.models.MyTransaction;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.Map;
import java.util.Objects;

public class TransactionDeserializer implements Deserializer<MyTransaction> {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {

    }

    @Override
    public MyTransaction deserialize(String topic, byte[] bytes) {
        if (Objects.isNull(bytes)) {
            return null;
        }

        MyTransaction data = new MyTransaction();
        try {
            data = objectMapper.treeToValue(objectMapper.readTree(bytes), MyTransaction.class);
        } catch (Exception e) {
            System.out.println("Exception occured while deserializing MyTransactions -> " + e.getMessage());
        }

        return data;
    }

    @Override
    public void close() {

    }
}

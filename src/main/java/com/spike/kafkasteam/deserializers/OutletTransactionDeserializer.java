package com.spike.kafkasteam.deserializers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spike.kafkasteam.models.OutletTransaction;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.Map;
import java.util.Objects;

public class OutletTransactionDeserializer implements Deserializer<OutletTransaction> {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {

    }

    @Override
    public OutletTransaction deserialize(String topic, byte[] bytes) {
        if (Objects.isNull(bytes)) {
            return null;
        }

        OutletTransaction data = new OutletTransaction();
        try {
            data = objectMapper.treeToValue(objectMapper.readTree(bytes), OutletTransaction.class);
        } catch (Exception e) {
            System.out.println("Exception occured while deserializing OutletTransaction -> " + e.getMessage());
        }

        return data;
    }

    @Override
    public void close() {

    }
}

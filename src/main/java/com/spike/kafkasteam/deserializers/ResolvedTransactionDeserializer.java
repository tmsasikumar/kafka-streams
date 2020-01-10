package com.spike.kafkasteam.deserializers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spike.kafkasteam.models.ResolvedTransaction;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.Map;
import java.util.Objects;

public class ResolvedTransactionDeserializer implements Deserializer<ResolvedTransaction> {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {

    }

    @Override
    public ResolvedTransaction deserialize(String topic, byte[] bytes) {
        if (Objects.isNull(bytes)) {
            return null;
        }

        ResolvedTransaction data = null;
        try {
            data = objectMapper.treeToValue(objectMapper.readTree(bytes), ResolvedTransaction.class);
        } catch (Exception e) {
            System.out.println("Exception occured while deserializing ResolvedTransaction -> " + e.getMessage());
        }

        return data;
    }

    @Override
    public void close() {

    }
}

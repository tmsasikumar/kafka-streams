package com.spike.kafkasteam.deserializers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spike.kafkasteam.models.ResolvedTransactionOutlet;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.Map;
import java.util.Objects;

public class ResolvedTransactionOutletDeserializer implements Deserializer<ResolvedTransactionOutlet> {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {

    }

    @Override
    public ResolvedTransactionOutlet deserialize(String topic, byte[] bytes) {
        if (Objects.isNull(bytes)) {
            return null;
        }

        ResolvedTransactionOutlet data = null;
        try {
            data = objectMapper.treeToValue(objectMapper.readTree(bytes), ResolvedTransactionOutlet.class);
        } catch (Exception e) {
            System.out.println("Exception occured while deserializing ResolvedTransactionOutlet -> " + e.getMessage());
        }

        return data;
    }

    @Override
    public void close() {

    }
}

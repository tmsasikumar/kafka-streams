package com.spike.kafkasteam;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.errors.SerializationException;
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

        ResolvedTransactionOutlet data;
        try {
            data = objectMapper.treeToValue(objectMapper.readTree(bytes), ResolvedTransactionOutlet.class);
        } catch (Exception e) {
            throw new SerializationException(e);
        }

        return data;
    }

    @Override
    public void close() {

    }
}
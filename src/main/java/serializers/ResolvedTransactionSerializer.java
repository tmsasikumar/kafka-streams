package serializers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spike.kafkasteam.models.ResolvedTransaction;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;
import java.util.Objects;

public class ResolvedTransactionSerializer implements Serializer<ResolvedTransaction> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {

    }

    @Override
    public byte[] serialize(String topic, ResolvedTransaction data) {

        if (Objects.isNull(data)) {
            return null;
        }
        try {
            return objectMapper.writeValueAsBytes(data);
        } catch (Exception e) {
            System.out.println("Exception occured while serializing ResolvedTransaction -> " + e.getMessage());
            return null;
        }
    }

    @Override
    public void close() {

    }
}



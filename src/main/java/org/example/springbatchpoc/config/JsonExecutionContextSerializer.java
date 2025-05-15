package org.example.springbatchpoc.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.batch.core.repository.ExecutionContextSerializer;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

@Component
public class JsonExecutionContextSerializer implements ExecutionContextSerializer {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void serialize(Map<String, Object> context, OutputStream out) throws IOException {
        objectMapper.writeValue(out, context);
    }
@Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> deserialize(InputStream inputStream) throws IOException {
        return objectMapper.readValue(inputStream, HashMap.class);
    }
}
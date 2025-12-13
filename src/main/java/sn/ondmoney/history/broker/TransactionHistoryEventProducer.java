package sn.ondmoney.history.broker;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import sn.ondmoney.history.service.dto.HistoryEventDTO;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component("transactionHistoryEventProducer")
public class TransactionHistoryEventProducer implements Supplier<Message<String>> {

    private static final Logger LOG = LoggerFactory.getLogger(TransactionHistoryEventProducer.class);

    private final BlockingQueue<HistoryEventDTO> queue = new LinkedBlockingQueue<>();
    private final ObjectMapper objectMapper;

    public TransactionHistoryEventProducer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.enable(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN);    }

    @Override
    public Message<String> get() {
        HistoryEventDTO event = queue.poll();
        if (event == null) return null;

        try {
            String json = objectMapper.writeValueAsString(event);
            LOG.info("Publishing history event: type={}, transactionId={}", event.getEventType(), event.getTransactionId());
            return MessageBuilder.withPayload(json)
                .setHeader("contentType", "application/json")
                .build();
        } catch (JsonProcessingException e) {
            LOG.error("Failed to serialize HistoryEventDTO", e);
            return null;
        }
    }

    public void publish(HistoryEventDTO event) {
        LOG.debug("Queueing history event: {}", event.getTransactionId());
        queue.offer(event);
    }
}

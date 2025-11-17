package com.ondmoney.sn.history.broker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ondmoney.sn.history.service.dto.HistoryEventDTO;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
public class TransactionEventProducer implements Supplier<org.springframework.messaging.Message<String>> {

    private static final Logger LOG = LoggerFactory.getLogger(TransactionEventProducer.class);

    private final BlockingQueue<HistoryEventDTO> queue = new LinkedBlockingQueue<>();
    private final ObjectMapper objectMapper;

    public TransactionEventProducer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Called automatically by Spring Cloud Stream to publish messages to Kafka
     */
    @Override
    public org.springframework.messaging.Message<String> get() {
        HistoryEventDTO event = queue.poll();
        if (event == null) {
            return null; // nothing to send
        }

        try {
            String jsonPayload = objectMapper.writeValueAsString(event);

            LOG.info("Publishing transaction history event: type={}, transactionId={}", event.getEventType(), event.getTransactionId());

            return MessageBuilder.withPayload(jsonPayload).setHeader("contentType", "application/json").build();
        } catch (JsonProcessingException e) {
            LOG.error("Failed to serialize HistoryEventDTO", e);
            return null;
        }
    }

    /**
     * Method used by the application to enqueue events for Kafka publishing
     */
    public void publish(HistoryEventDTO event) {
        LOG.debug("Queueing history event for publication: {}", event.getTransactionId());
        queue.offer(event);
    }
}

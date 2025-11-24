package sn.ondmoney.history.broker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import sn.ondmoney.history.domain.TransactionHistory;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component("transactionTxnEventProducer")
public class TransactionTxnEventProducer implements Supplier<Message<String>> {

    private static final Logger LOG = LoggerFactory.getLogger(TransactionTxnEventProducer.class);

    private final BlockingQueue<TransactionHistory> queue = new LinkedBlockingQueue<>();
    private final ObjectMapper objectMapper;

    public TransactionTxnEventProducer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Message<String> get() {
        TransactionHistory tx = queue.poll();
        if (tx == null) return null;

        try {
            String json = objectMapper.writeValueAsString(tx);
            LOG.info("Publishing raw transaction: {}", tx.getTransactionId());
            return MessageBuilder.withPayload(json)
                .setHeader("contentType", "application/json")
                .build();
        } catch (JsonProcessingException e) {
            LOG.error("Failed to serialize TransactionHistory", e);
            return null;
        }
    }

    public void publish(TransactionHistory tx) {
        LOG.debug("Queueing raw transaction: {}", tx.getTransactionId());
        queue.offer(tx);
    }
}

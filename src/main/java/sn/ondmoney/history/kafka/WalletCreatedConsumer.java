package sn.ondmoney.history.kafka;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import sn.ondmoney.history.domain.ProcessedEvent;
import sn.ondmoney.history.domain.TransactionHistory;
import sn.ondmoney.history.domain.enumeration.TransactionStatus;
import sn.ondmoney.history.domain.enumeration.TransactionType;
import sn.ondmoney.history.repository.ProcessedEventRepository;
import sn.ondmoney.history.repository.TransactionHistoryRepository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Kafka consumer for wallet.created events.
 * Logs wallet creation in transaction history.
 */
@Component
public class WalletCreatedConsumer {

    private static final Logger LOG = LoggerFactory.getLogger(WalletCreatedConsumer.class);
    private static final String TOPIC = "wallet.created";

    private final TransactionHistoryRepository historyRepository;
    private final ProcessedEventRepository processedEventRepository;
    private final ObjectMapper objectMapper;

    public WalletCreatedConsumer(
            TransactionHistoryRepository historyRepository,
            ProcessedEventRepository processedEventRepository,
            ObjectMapper objectMapper) {
        this.historyRepository = historyRepository;
        this.processedEventRepository = processedEventRepository;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(
        topics = TOPIC,
        groupId = "history-consumers"
    )
    public void handleWalletCreated(
            @Payload String payload,
            @Header(KafkaHeaders.RECEIVED_KEY) String key,
            @Header(value = "ce_id", required = false) String eventId) {

        LOG.info("Received wallet.created event for key: {}", key);

        try {
            JsonNode cloudEvent = objectMapper.readTree(payload);

            if (eventId == null || eventId.isBlank()) {
                eventId = cloudEvent.has("id") ? cloudEvent.get("id").asText() : key + "-" + System.currentTimeMillis();
            }

            // Idempotency check
            if (processedEventRepository.existsByEventId(eventId)) {
                LOG.info("Event {} already processed, skipping", eventId);
                return;
            }

            JsonNode dataNode = cloudEvent.get("data");
            if (dataNode == null) {
                LOG.error("No data found in CloudEvents envelope");
                return;
            }

            // Extract wallet info
            String walletId = dataNode.has("walletId") ? dataNode.get("walletId").asText() : null;
            String userId = dataNode.has("userId") ? dataNode.get("userId").asText() : key;
            String currency = dataNode.has("currency") ? dataNode.get("currency").asText() : "XOF";
            BigDecimal initialBalance = dataNode.has("initialBalance") 
                ? new BigDecimal(dataNode.get("initialBalance").asText()) 
                : BigDecimal.ZERO;

            // Extract correlation ID
            String correlationId = eventId;
            if (cloudEvent.has("ondmoney") && cloudEvent.get("ondmoney").has("correlationId")) {
                correlationId = cloudEvent.get("ondmoney").get("correlationId").asText();
            }

            // Create history record
            TransactionHistory history = new TransactionHistory();
            history.setTransactionId("txn_wallet_" + UUID.randomUUID().toString());
            history.setUserId(userId);
            history.setType(TransactionType.WALLET_CREATION);
            history.setAmount(initialBalance);
            history.setCurrency(currency);
            history.setBalanceAfter(initialBalance);
            history.setStatus(TransactionStatus.COMPLETED);
            history.setDescription("Wallet created: " + walletId);
            history.setCorrelationId(correlationId);
            history.setTransactionDate(Instant.now());
            history.setProcessingDate(Instant.now());
            history.setHistorySaved(true);
            history.setSenderPhone(userId); // Set sender phone as user ID for wallet creation

            historyRepository.save(history);

            processedEventRepository.save(new ProcessedEvent(eventId, TOPIC));

            LOG.info("Successfully logged wallet creation for user: {}", userId);

        } catch (Exception e) {
            LOG.error("Error processing wallet.created event: {}", e.getMessage(), e);
        }
    }
}

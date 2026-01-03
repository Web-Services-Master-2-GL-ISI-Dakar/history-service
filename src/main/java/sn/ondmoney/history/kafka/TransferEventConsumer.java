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

/**
 * Kafka consumer for transfer events (initiated, completed, failed).
 * Logs transfer history for both sender and receiver.
 */
@Component
public class TransferEventConsumer {

    private static final Logger LOG = LoggerFactory.getLogger(TransferEventConsumer.class);

    private final TransactionHistoryRepository historyRepository;
    private final ProcessedEventRepository processedEventRepository;
    private final ObjectMapper objectMapper;

    public TransferEventConsumer(
            TransactionHistoryRepository historyRepository,
            ProcessedEventRepository processedEventRepository,
            ObjectMapper objectMapper) {
        this.historyRepository = historyRepository;
        this.processedEventRepository = processedEventRepository;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(
        topics = {"transfer.initiated", "transfer.completed", "transfer.failed"},
        groupId = "history-consumers"
    )
    public void handleTransferEvent(
            @Payload String payload,
            @Header(KafkaHeaders.RECEIVED_KEY) String key,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(value = "ce_id", required = false) String eventId) {

        LOG.info("Received {} event for key: {}", topic, key);

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

            // Extract correlation ID
            String correlationId = eventId;
            if (cloudEvent.has("ondmoney") && cloudEvent.get("ondmoney").has("correlationId")) {
                correlationId = cloudEvent.get("ondmoney").get("correlationId").asText();
            }

            // Extract transfer data
            String transferId = dataNode.has("transferId") ? dataNode.get("transferId").asText() : null;
            String senderId = dataNode.has("senderId") ? dataNode.get("senderId").asText() : null;
            String receiverId = dataNode.has("receiverId") ? dataNode.get("receiverId").asText() : null;
            BigDecimal amount = dataNode.has("amount") ? new BigDecimal(dataNode.get("amount").asText()) : BigDecimal.ZERO;
            String currency = dataNode.has("currency") ? dataNode.get("currency").asText() : "XOF";
            String description = dataNode.has("description") ? dataNode.get("description").asText() : "Transfer";

            TransactionStatus status = switch (topic) {
                case "transfer.initiated" -> TransactionStatus.PENDING;
                case "transfer.completed" -> TransactionStatus.COMPLETED;
                case "transfer.failed" -> TransactionStatus.FAILED;
                default -> TransactionStatus.PENDING;
            };

            // For completed transfers, create history for both sender and receiver
            if ("transfer.completed".equals(topic)) {
                // Sender debit record
                BigDecimal senderNewBalance = dataNode.has("senderNewBalance") 
                    ? new BigDecimal(dataNode.get("senderNewBalance").asText()) 
                    : null;
                String senderName = dataNode.has("senderName") ? dataNode.get("senderName").asText() : null;
                String receiverName = dataNode.has("receiverName") ? dataNode.get("receiverName").asText() : null;
                String senderPhone = dataNode.has("senderPhoneNumber") ? dataNode.get("senderPhoneNumber").asText() : null;
                String receiverPhone = dataNode.has("receiverPhoneNumber") ? dataNode.get("receiverPhoneNumber").asText() : null;

                TransactionHistory senderHistory = new TransactionHistory();
                senderHistory.setTransactionId(transferId + "_sender");
                senderHistory.setUserId(senderId);
                senderHistory.setType(TransactionType.TRANSFER);
                senderHistory.setAmount(amount);
                senderHistory.setCurrency(currency);
                senderHistory.setBalanceAfter(senderNewBalance);
                senderHistory.setStatus(status);
                senderHistory.setDescription(description);
                senderHistory.setCorrelationId(correlationId);
                senderHistory.setCounterpartyId(receiverId);
                senderHistory.setCounterpartyName(receiverName);
                senderHistory.setSenderPhone(senderPhone);
                senderHistory.setReceiverPhone(receiverPhone);
                senderHistory.setSenderName(senderName);
                senderHistory.setReceiverName(receiverName);
                senderHistory.setTransactionDate(Instant.now());
                senderHistory.setProcessingDate(Instant.now());
                senderHistory.setHistorySaved(true);
                historyRepository.save(senderHistory);

                // Receiver credit record
                BigDecimal receiverNewBalance = dataNode.has("receiverNewBalance") 
                    ? new BigDecimal(dataNode.get("receiverNewBalance").asText()) 
                    : null;

                TransactionHistory receiverHistory = new TransactionHistory();
                receiverHistory.setTransactionId(transferId + "_receiver");
                receiverHistory.setUserId(receiverId);
                receiverHistory.setType(TransactionType.TRANSFER);
                receiverHistory.setAmount(amount);
                receiverHistory.setCurrency(currency);
                receiverHistory.setBalanceAfter(receiverNewBalance);
                receiverHistory.setStatus(status);
                receiverHistory.setDescription(description);
                receiverHistory.setCorrelationId(correlationId);
                receiverHistory.setCounterpartyId(senderId);
                receiverHistory.setCounterpartyName(senderName);
                receiverHistory.setSenderPhone(senderPhone);
                receiverHistory.setReceiverPhone(receiverPhone);
                receiverHistory.setSenderName(senderName);
                receiverHistory.setReceiverName(receiverName);
                receiverHistory.setTransactionDate(Instant.now());
                receiverHistory.setProcessingDate(Instant.now());
                receiverHistory.setHistorySaved(true);
                historyRepository.save(receiverHistory);

                LOG.info("Created history records for completed transfer: {}", transferId);
            } else {
                // For initiated/failed, just create a single record for the sender
                TransactionHistory history = new TransactionHistory();
                history.setTransactionId(transferId);
                history.setUserId(senderId);
                history.setType(TransactionType.TRANSFER);
                history.setAmount(amount);
                history.setCurrency(currency);
                history.setStatus(status);
                history.setDescription(description);
                history.setCorrelationId(correlationId);
                history.setCounterpartyId(receiverId);
                history.setSenderPhone(senderId); // Use senderId as phone placeholder
                history.setTransactionDate(Instant.now());
                history.setProcessingDate(Instant.now());
                history.setHistorySaved(true);

                if ("transfer.failed".equals(topic)) {
                    String failureReason = dataNode.has("failureReason") ? dataNode.get("failureReason").asText() : null;
                    String failureMessage = dataNode.has("failureMessage") ? dataNode.get("failureMessage").asText() : null;
                    history.setErrorMessage(failureMessage != null ? failureMessage : failureReason);
                }

                historyRepository.save(history);
                LOG.info("Created history record for {} transfer: {}", topic, transferId);
            }

            processedEventRepository.save(new ProcessedEvent(eventId, topic));

        } catch (Exception e) {
            LOG.error("Error processing {} event: {}", topic, e.getMessage(), e);
        }
    }
}

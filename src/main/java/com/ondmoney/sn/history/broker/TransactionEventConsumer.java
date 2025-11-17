package com.ondmoney.sn.history.broker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ondmoney.sn.history.domain.TransactionHistory;
import com.ondmoney.sn.history.domain.enumeration.HistoryEventType;
import com.ondmoney.sn.history.repository.TransactionHistoryRepository;
import com.ondmoney.sn.history.service.dto.HistoryEventDTO;
import java.time.Instant;
import java.util.UUID;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
public class TransactionEventConsumer {

    private static final Logger LOG = LoggerFactory.getLogger(TransactionEventConsumer.class);

    private final TransactionHistoryRepository transactionHistoryRepository;
    private final TransactionEventProducer historyEventProducer;
    private final ObjectMapper objectMapper;

    public TransactionEventConsumer(
        TransactionHistoryRepository transactionHistoryRepository,
        TransactionEventProducer historyEventProducer,
        ObjectMapper objectMapper
    ) {
        this.transactionHistoryRepository = transactionHistoryRepository;
        this.historyEventProducer = historyEventProducer;
        this.objectMapper = objectMapper;
    }

    @Bean
    public Consumer<Message<String>> transactionConsumer() {
        return message -> {
            try {
                LOG.debug("Received transaction event: {}", message.getPayload());

                // Convertir le message en objet TransactionHistory
                TransactionHistory transaction = objectMapper.readValue(message.getPayload(), TransactionHistory.class);

                // S'assurer que l'ID est présent
                if (transaction.getTransactionId() == null) {
                    transaction.setTransactionId(UUID.randomUUID().toString());
                }

                // Marquer comme sauvegardé
                transaction.setHistorySaved(true);
                transaction.setProcessingDate(Instant.now());

                // Sauvegarder dans MongoDB
                TransactionHistory savedTransaction = transactionHistoryRepository.save(transaction);
                LOG.info("Transaction history saved successfully: {}", savedTransaction.getTransactionId());

                // Publier un événement de succès
                HistoryEventDTO successEvent = new HistoryEventDTO();
                successEvent.setEventType(HistoryEventType.HISTORY_SAVED);
                successEvent.setTransactionId(savedTransaction.getTransactionId());
                successEvent.setTimestamp(Instant.now());
                successEvent.setMessage("Transaction history saved successfully");
                successEvent.setCorrelationId(savedTransaction.getCorrelationId());

                historyEventProducer.publish(successEvent);
            } catch (Exception e) {
                LOG.error("Error processing transaction event: {}", message.getPayload(), e);

                // Publier un événement d'échec
                try {
                    HistoryEventDTO failedEvent = new HistoryEventDTO();
                    failedEvent.setEventType(HistoryEventType.HISTORY_FAILED);
                    failedEvent.setTimestamp(Instant.now());
                    failedEvent.setMessage("Failed to save transaction history: " + e.getMessage());

                    historyEventProducer.publish(failedEvent);
                } catch (Exception ex) {
                    LOG.error("Failed to send error event", ex);
                }
            }
        };
    }
}

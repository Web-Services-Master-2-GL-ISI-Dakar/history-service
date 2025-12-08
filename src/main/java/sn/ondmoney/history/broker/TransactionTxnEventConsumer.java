package sn.ondmoney.history.broker;

import com.fasterxml.jackson.databind.ObjectMapper;
import sn.ondmoney.history.domain.TransactionHistory;
import sn.ondmoney.history.domain.enumeration.HistoryEventType;
import sn.ondmoney.history.repository.TransactionHistoryRepository;
import sn.ondmoney.history.repository.search.TransactionHistorySearchRepository;
import sn.ondmoney.history.service.dto.HistoryEventDTO;
import java.time.Instant;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("transactionTxnEventConsumer")
public class TransactionTxnEventConsumer implements Consumer<String> {

    private static final Logger LOG = LoggerFactory.getLogger(TransactionTxnEventConsumer.class);

    private final TransactionHistoryRepository repository;
    private final TransactionHistorySearchRepository searchRepository;
    private final TransactionHistoryEventProducer historyProducer;
    private final ObjectMapper objectMapper;

    public TransactionTxnEventConsumer(
        TransactionHistoryRepository repository,
        TransactionHistorySearchRepository searchRepository,
        TransactionHistoryEventProducer historyProducer,
        ObjectMapper objectMapper) {
        this.repository = repository;
        this.searchRepository = searchRepository;
        this.historyProducer = historyProducer;
        this.objectMapper = objectMapper;
    }

    @Override
    public void accept(String payload) {
        LOG.info("<<< Consumed transaction event: {}", payload);
        try {
            TransactionHistory tx = objectMapper.readValue(payload, TransactionHistory.class);

            // --- Idempotency check ---
            if (repository.existsByTransactionId(tx.getTransactionId())) {
                LOG.warn("Duplicate transaction ignored: {}", tx.getTransactionId());
                return;
            }

            // --- Persist transaction in MongoDB ---
            tx.setHistorySaved(true);
            tx.setProcessingDate(Instant.now());
            TransactionHistory savedTx = repository.save(tx);
            LOG.info("Transaction saved successfully in MongoDB: {}", savedTx.getTransactionId());

            // --- Index transaction in Elasticsearch ---
            searchRepository.index(savedTx);
            LOG.info("Transaction indexed successfully in Elasticsearch: {}", savedTx.getTransactionId());

            // --- Publish success event ---
            HistoryEventDTO successEvent = new HistoryEventDTO();
            successEvent.setEventType(HistoryEventType.HISTORY_SAVED);
            successEvent.setTransactionId(savedTx.getTransactionId());
            successEvent.setTimestamp(Instant.now());
            successEvent.setMessage("Transaction history saved and indexed successfully");
            successEvent.setCorrelationId(savedTx.getCorrelationId());
            historyProducer.publish(successEvent);

        } catch (Exception e) {
            LOG.error("Error processing transaction event: {}", payload, e);

            try {
                HistoryEventDTO failedEvent = new HistoryEventDTO();
                failedEvent.setEventType(HistoryEventType.HISTORY_FAILED);
                failedEvent.setTimestamp(Instant.now());
                failedEvent.setMessage("Failed to save or index transaction: " + e.getMessage());
                historyProducer.publish(failedEvent);
            } catch (Exception ex) {
                LOG.error("Failed to send failure event", ex);
            }
        }
    }
}

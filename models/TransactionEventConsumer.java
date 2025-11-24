package com.ondmoney.sn.history.service;

import com.ondmoney.sn.history.domain.TransactionHistory;
import com.ondmoney.sn.history.domain.enumeration.TransactionStatus;
import com.ondmoney.sn.history.domain.enumeration.TransactionType;
import com.ondmoney.sn.history.repository.TransactionHistoryRepository;
import com.ondmoney.sn.history.repository.search.TransactionHistorySearchRepository;
import com.ondmoney.sn.history.service.dto.TransactionHistoryDTO;
import java.time.Instant;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

@Service
public class TransactionEventConsumer {

    private final Logger log = LoggerFactory.getLogger(TransactionEventConsumer.class);

    private final TransactionHistoryRepository transactionHistoryRepository;
    private final TransactionHistorySearchRepository transactionHistorySearchRepository;
    private final TransactionHistoryService transactionHistoryService;

    public TransactionEventConsumer(
        TransactionHistoryRepository transactionHistoryRepository,
        TransactionHistorySearchRepository transactionHistorySearchRepository,
        TransactionHistoryService transactionHistoryService
    ) {
        this.transactionHistoryRepository = transactionHistoryRepository;
        this.transactionHistorySearchRepository = transactionHistorySearchRepository;
        this.transactionHistoryService = transactionHistoryService;
    }

    @Bean
    public Consumer<TransactionEventProducer.TransactionEvent> transactionConsumer() {
        return event -> {
            try {
                log.info(
                    "📥 Événement reçu via Spring Cloud Stream: {} - {} - {} XOF",
                    event.getTransactionId(),
                    event.getType(),
                    event.getAmount()
                );

                // Convertir l'événement en DTO pour le service
                TransactionHistoryDTO dto = convertEventToDTO(event);

                log.debug("DTO créé: {}", dto.getTransactionId());

                // Sauvegarder via le service qui gère les DTOs
                TransactionHistoryDTO savedDTO = transactionHistoryService.save(dto);

                log.info(
                    "✅ Transaction historisée via DTO: {} - {} - {}",
                    savedDTO.getTransactionId(),
                    savedDTO.getType(),
                    savedDTO.getAmount()
                );
            } catch (Exception e) {
                log.error("❌ Erreur lors du traitement de l'événement {}: {}", event.getTransactionId(), e.getMessage(), e);
            }
        };
    }

    private TransactionHistoryDTO convertEventToDTO(TransactionEventProducer.TransactionEvent event) {
        TransactionHistoryDTO dto = new TransactionHistoryDTO();

        // Champs obligatoires
        dto.setTransactionId(event.getTransactionId());
        dto.setType(event.getType());
        dto.setStatus(event.getStatus());
        dto.setAmount(event.getAmount());
        dto.setCurrency(event.getCurrency());
        dto.setSenderPhone(event.getSenderPhone());
        dto.setTransactionDate(event.getTransactionDate());
        dto.setHistorySaved(true);

        // Champs optionnels
        if (event.getReceiverPhone() != null) {
            dto.setReceiverPhone(event.getReceiverPhone());
        }
        if (event.getSenderName() != null) {
            dto.setSenderName(event.getSenderName());
        }
        if (event.getReceiverName() != null) {
            dto.setReceiverName(event.getReceiverName());
        }
        if (event.getDescription() != null) {
            dto.setDescription(event.getDescription());
        }
        if (event.getFees() != null) {
            dto.setFees(event.getFees());
        }
        if (event.getBalanceBefore() != null) {
            dto.setBalanceBefore(event.getBalanceBefore());
        }
        if (event.getBalanceAfter() != null) {
            dto.setBalanceAfter(event.getBalanceAfter());
        }
        if (event.getMerchantCode() != null) {
            dto.setMerchantCode(event.getMerchantCode());
        }
        if (event.getBillReference() != null) {
            dto.setBillReference(event.getBillReference());
        }
        if (event.getBankAccountNumber() != null) {
            dto.setBankAccountNumber(event.getBankAccountNumber());
        }
        if (event.getProcessingDate() != null) {
            dto.setProcessingDate(event.getProcessingDate());
        } else {
            dto.setProcessingDate(Instant.now());
        }
        if (event.getCreatedBy() != null) {
            dto.setCreatedBy(event.getCreatedBy());
        }
        if (event.getUserAgent() != null) {
            dto.setUserAgent(event.getUserAgent());
        }
        if (event.getIpAddress() != null) {
            dto.setIpAddress(event.getIpAddress());
        }
        if (event.getDeviceId() != null) {
            dto.setDeviceId(event.getDeviceId());
        }
        if (event.getMetadata() != null) {
            dto.setMetadata(event.getMetadata());
        }
        if (event.getErrorMessage() != null) {
            dto.setErrorMessage(event.getErrorMessage());
        }
        if (event.getCorrelationId() != null) {
            dto.setCorrelationId(event.getCorrelationId());
        }
        if (event.getVersion() != null) {
            dto.setVersion(event.getVersion());
        } else {
            dto.setVersion(1);
        }

        return dto;
    }
}

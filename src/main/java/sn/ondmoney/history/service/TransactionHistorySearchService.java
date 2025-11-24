package sn.ondmoney.history.service;

import sn.ondmoney.history.domain.TransactionHistory;
import sn.ondmoney.history.domain.enumeration.TransactionStatus;
import sn.ondmoney.history.domain.enumeration.TransactionType;
import sn.ondmoney.history.repository.search.TransactionHistorySearchRepository;
import java.math.BigDecimal;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class TransactionHistorySearchService {

    private static final Logger LOG = LoggerFactory.getLogger(TransactionHistorySearchService.class);

    private final TransactionHistorySearchRepository transactionHistorySearchRepository;

    public TransactionHistorySearchService(TransactionHistorySearchRepository transactionHistorySearchRepository) {
        this.transactionHistorySearchRepository = transactionHistorySearchRepository;
    }

    public Page<TransactionHistory> searchTransactions(String query, Pageable pageable) {
        LOG.debug("Searching transactions with query: {}", query);
        return transactionHistorySearchRepository.search(query, pageable);
    }

    public Page<TransactionHistory> findBySenderPhone(String senderPhone, Pageable pageable) {
        LOG.debug("Searching transactions by sender phone: {}", senderPhone);
        return transactionHistorySearchRepository.findBySenderPhone(senderPhone, pageable);
    }

    public Page<TransactionHistory> findByReceiverPhone(String receiverPhone, Pageable pageable) {
        LOG.debug("Searching transactions by receiver phone: {}", receiverPhone);
        return transactionHistorySearchRepository.findByReceiverPhone(receiverPhone, pageable);
    }

    public Page<TransactionHistory> findByTransactionDateBetween(Instant startDate, Instant endDate, Pageable pageable) {
        LOG.debug("Searching transactions between {} and {}", startDate, endDate);
        return transactionHistorySearchRepository.findByTransactionDateBetween(startDate, endDate, pageable);
    }

    public Page<TransactionHistory> findByAmountBetween(BigDecimal minAmount, BigDecimal maxAmount, Pageable pageable) {
        LOG.debug("Searching transactions with amount between {} and {}", minAmount, maxAmount);
        return transactionHistorySearchRepository.findByAmountBetween(minAmount, maxAmount, pageable);
    }

    public Page<TransactionHistory> findByTypeAndStatus(String type, String status, Pageable pageable) {
        LOG.debug("Searching transactions by type: {} and status: {}", type, status);
        return transactionHistorySearchRepository.findByTypeAndStatus(type, status, pageable);
    }

    // Recherche avancée avec filtres multiples
    public Page<TransactionHistory> advancedSearch(
        String senderPhone,
        String receiverPhone,
        TransactionType type,
        TransactionStatus status,
        Instant startDate,
        Instant endDate,
        BigDecimal minAmount,
        BigDecimal maxAmount,
        Pageable pageable
    ) {
        LOG.debug(
            "Advanced search - sender: {}, receiver: {}, type: {}, status: {}, dateRange: {}-{}, amountRange: {}-{}",
            senderPhone,
            receiverPhone,
            type,
            status,
            startDate,
            endDate,
            minAmount,
            maxAmount
        );

        return transactionHistorySearchRepository.searchByCriteria(
            senderPhone,
            receiverPhone,
            type,
            status,
            startDate,
            endDate,
            minAmount,
            maxAmount,
            pageable
        );
    }

    // Nouvelle méthode pour la recherche avec QUERYSTRING comme l'original
    public Page<TransactionHistory> searchByCriteria(
        String phoneNumber,
        TransactionType type,
        TransactionStatus status,
        String query,
        Instant startDate,
        Instant endDate,
        BigDecimal minAmount,
        BigDecimal maxAmount,
        Pageable pageable
    ) {
        LOG.debug(
            "Search by criteria - phone: {}, type: {}, status: {}, query: {}, dateRange: {}-{}, amountRange: {}-{}",
            phoneNumber,
            type,
            status,
            query,
            startDate,
            endDate,
            minAmount,
            maxAmount
        );

        // Construction de la requête QUERYSTRING comme la recherche originale
        StringBuilder queryString = new StringBuilder();

        // Recherche par numéro de téléphone (expéditeur OU destinataire)
        if (phoneNumber != null && !phoneNumber.trim().isEmpty()) {
            queryString.append("(senderPhone:").append(phoneNumber).append(" OR receiverPhone:").append(phoneNumber).append(")");
        }

        // Recherche par type
        if (type != null) {
            if (queryString.length() > 0) queryString.append(" AND ");
            queryString.append("type:").append(type);
        }

        // Recherche par statut
        if (status != null) {
            if (queryString.length() > 0) queryString.append(" AND ");
            queryString.append("status:").append(status);
        }

        // Recherche textuelle (comme la recherche originale)
        if (query != null && !query.trim().isEmpty()) {
            if (queryString.length() > 0) queryString.append(" AND ");
            queryString
                .append("(")
                .append("description:")
                .append(query)
                .append(" OR senderName:")
                .append(query)
                .append(" OR receiverName:")
                .append(query)
                .append(" OR transactionId:")
                .append(query)
                .append(" OR externalTransactionId:")
                .append(query)
                .append(")");
        }

        // Recherche par plage de dates
        if (startDate != null && endDate != null) {
            if (queryString.length() > 0) queryString.append(" AND ");
            queryString.append("transactionDate:[").append(startDate).append(" TO ").append(endDate).append("]");
        }

        // Recherche par plage de montants
        if (minAmount != null && maxAmount != null) {
            if (queryString.length() > 0) queryString.append(" AND ");
            queryString.append("amount:[").append(minAmount).append(" TO ").append(maxAmount).append("]");
        }

        String finalQuery = queryString.toString();

        // Si aucun critère n'est spécifié, on retourne tous les résultats
        if (finalQuery.isEmpty()) {
            finalQuery = "*";
        }

        LOG.debug("Executing QUERYSTRING search: {}", finalQuery);
        return transactionHistorySearchRepository.search(finalQuery, pageable);
    }
}

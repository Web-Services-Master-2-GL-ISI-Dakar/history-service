package sn.ondmoney.history.service;

import sn.ondmoney.history.domain.TransactionHistory;
import sn.ondmoney.history.domain.enumeration.*;
import sn.ondmoney.history.repository.search.TransactionHistorySearchRepository;
import sn.ondmoney.history.web.graphql.response.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TransactionHistorySearchService {

    private static final Logger LOG = LoggerFactory.getLogger(TransactionHistorySearchService.class);

    private final TransactionHistorySearchRepository transactionHistorySearchRepository;

    public TransactionHistorySearchService(TransactionHistorySearchRepository transactionHistorySearchRepository) {
        this.transactionHistorySearchRepository = transactionHistorySearchRepository;
    }

    // Search with all new criteria
    public Page<TransactionHistory> advancedSearch(
        String senderPhone,
        String receiverPhone,
        List<TransactionType> types,
        List<TransactionStatus> statuses,
        Instant startDate,
        Instant endDate,
        BigDecimal minAmount,
        BigDecimal maxAmount,
        String currency,
        TransactionDirection direction,
        String merchantCode,
        String billReference,
        String bankAccountNumber,
        String descriptionContains,
        Pageable pageable
    ) {
        LOG.debug(
            "Advanced search - sender: {}, receiver: {}, types: {}, statuses: {}, dateRange: {}-{}, amountRange: {}-{}, currency: {}, direction: {}, merchantCode: {}, billRef: {}, bankAcc: {}, descriptionContains: {}",
            senderPhone,
            receiverPhone,
            types,
            statuses,
            startDate,
            endDate,
            minAmount,
            maxAmount,
            currency,
            direction,
            merchantCode,
            billReference,
            bankAccountNumber,
            descriptionContains
        );

        // Build query string based on direction
        String finalQuery = buildQueryString(
            senderPhone, receiverPhone, types, statuses, startDate, endDate,
            minAmount, maxAmount, currency, direction, merchantCode,
            billReference, bankAccountNumber, descriptionContains
        );

        LOG.debug("Executing search query: {}", finalQuery);
        return transactionHistorySearchRepository.search(finalQuery, pageable);
    }

    // Get user transaction statistics
    public UserTransactionStats getUserTransactionStats(
        String phoneNumber,
        Instant startDate,
        Instant endDate,
        List<TransactionType> types,
        TransactionDirection direction
    ) {
        LOG.debug("Getting transaction stats for user: {}", phoneNumber);

        // Query for user's transactions
        String query = buildUserStatsQuery(phoneNumber, startDate, endDate, types, direction);
        Pageable pageable = Pageable.unpaged();
        Page<TransactionHistory> allTransactions = transactionHistorySearchRepository.search(query, pageable);

        List<TransactionHistory> transactions = allTransactions.getContent();

        if (transactions.isEmpty()) {
            return createEmptyStats(startDate, endDate);
        }

        return createUserTransactionStats(transactions, startDate, endDate);
    }

    // Helper method to build query string
    private String buildQueryString(
        String senderPhone,
        String receiverPhone,
        List<TransactionType> types,
        List<TransactionStatus> statuses,
        Instant startDate,
        Instant endDate,
        BigDecimal minAmount,
        BigDecimal maxAmount,
        String currency,
        TransactionDirection direction,
        String merchantCode,
        String billReference,
        String bankAccountNumber,
        String descriptionContains
    ) {
        StringBuilder queryBuilder = new StringBuilder();

        // Handle direction - if direction is specified, adjust phone filters
        if (direction != null) {
            switch (direction) {
                case SENT:
                    if (senderPhone != null) {
                        addAndClause(queryBuilder);
                        queryBuilder.append("senderPhone:").append(senderPhone);
                    }
                    break;
                case RECEIVED:
                    if (receiverPhone != null) {
                        addAndClause(queryBuilder);
                        queryBuilder.append("receiverPhone:").append(receiverPhone);
                    }
                    break;
                case ALL:
                    // For ALL, we need to search both sender and receiver
                    if (senderPhone != null && receiverPhone != null) {
                        addAndClause(queryBuilder);
                        queryBuilder.append("(senderPhone:").append(senderPhone)
                            .append(" OR receiverPhone:").append(receiverPhone).append(")");
                    } else if (senderPhone != null) {
                        addAndClause(queryBuilder);
                        queryBuilder.append("(senderPhone:").append(senderPhone)
                            .append(" OR receiverPhone:").append(senderPhone).append(")");
                    } else if (receiverPhone != null) {
                        addAndClause(queryBuilder);
                        queryBuilder.append("(senderPhone:").append(receiverPhone)
                            .append(" OR receiverPhone:").append(receiverPhone).append(")");
                    }
                    break;
            }
        } else {
            // No direction specified, use individual phone filters
            if (senderPhone != null) {
                addAndClause(queryBuilder);
                queryBuilder.append("senderPhone:").append(senderPhone);
            }
            if (receiverPhone != null) {
                addAndClause(queryBuilder);
                queryBuilder.append("receiverPhone:").append(receiverPhone);
            }
        }

        // Multiple types
        if (types != null && !types.isEmpty()) {
            addAndClause(queryBuilder);
            queryBuilder.append("(");
            for (int i = 0; i < types.size(); i++) {
                if (i > 0) queryBuilder.append(" OR ");
                queryBuilder.append("type:").append(types.get(i));
            }
            queryBuilder.append(")");
        }

        // Multiple statuses
        if (statuses != null && !statuses.isEmpty()) {
            addAndClause(queryBuilder);
            queryBuilder.append("(");
            for (int i = 0; i < statuses.size(); i++) {
                if (i > 0) queryBuilder.append(" OR ");
                queryBuilder.append("status:").append(statuses.get(i));
            }
            queryBuilder.append(")");
        }

        // Date range
        if (startDate != null && endDate != null) {
            addAndClause(queryBuilder);
            queryBuilder.append("transactionDate:[").append(startDate).append(" TO ").append(endDate).append("]");
        } else if (startDate != null) {
            addAndClause(queryBuilder);
            queryBuilder.append("transactionDate:[").append(startDate).append(" TO *]");
        } else if (endDate != null) {
            addAndClause(queryBuilder);
            queryBuilder.append("transactionDate:[* TO ").append(endDate).append("]");
        }

        // Amount range
        if (minAmount != null && maxAmount != null) {
            addAndClause(queryBuilder);
            queryBuilder.append("amount:[").append(minAmount).append(" TO ").append(maxAmount).append("]");
        } else if (minAmount != null) {
            addAndClause(queryBuilder);
            queryBuilder.append("amount:[").append(minAmount).append(" TO *]");
        } else if (maxAmount != null) {
            addAndClause(queryBuilder);
            queryBuilder.append("amount:[* TO ").append(maxAmount).append("]");
        }

        // Currency
        if (currency != null && !currency.trim().isEmpty()) {
            addAndClause(queryBuilder);
            queryBuilder.append("currency:").append(currency);
        }

        // Merchant code
        if (merchantCode != null && !merchantCode.trim().isEmpty()) {
            addAndClause(queryBuilder);
            queryBuilder.append("merchantCode:").append(merchantCode);
        }

        // Bill reference
        if (billReference != null && !billReference.trim().isEmpty()) {
            addAndClause(queryBuilder);
            queryBuilder.append("billReference:").append(billReference);
        }

        // Bank account number
        if (bankAccountNumber != null && !bankAccountNumber.trim().isEmpty()) {
            addAndClause(queryBuilder);
            queryBuilder.append("bankAccountNumber:").append(bankAccountNumber);
        }

        // Description contains (wildcard search)
        if (descriptionContains != null && !descriptionContains.trim().isEmpty()) {
            addAndClause(queryBuilder);
            queryBuilder.append("description:*").append(descriptionContains).append("*");
        }

        String finalQuery = queryBuilder.toString();
        return finalQuery.isEmpty() ? "*" : finalQuery;
    }

    private void addAndClause(StringBuilder queryBuilder) {
        if (queryBuilder.length() > 0) {
            queryBuilder.append(" AND ");
        }
    }

    // Build query for user statistics
    private String buildUserStatsQuery(
        String phoneNumber,
        Instant startDate,
        Instant endDate,
        List<TransactionType> types,
        TransactionDirection direction
    ) {
        StringBuilder queryBuilder = new StringBuilder();

        // User is involved in transaction (as sender or receiver based on direction)
        if (direction == null || direction == TransactionDirection.ALL) {
            queryBuilder.append("(senderPhone:").append(phoneNumber)
                .append(" OR receiverPhone:").append(phoneNumber).append(")");
        } else if (direction == TransactionDirection.SENT) {
            queryBuilder.append("senderPhone:").append(phoneNumber);
        } else if (direction == TransactionDirection.RECEIVED) {
            queryBuilder.append("receiverPhone:").append(phoneNumber);
        }

        // Date range
        if (startDate != null && endDate != null) {
            addAndClause(queryBuilder);
            queryBuilder.append("transactionDate:[").append(startDate).append(" TO ").append(endDate).append("]");
        }

        // Transaction types
        if (types != null && !types.isEmpty()) {
            addAndClause(queryBuilder);
            queryBuilder.append("(");
            for (int i = 0; i < types.size(); i++) {
                if (i > 0) queryBuilder.append(" OR ");
                queryBuilder.append("type:").append(types.get(i));
            }
            queryBuilder.append(")");
        }

        return queryBuilder.toString();
    }

    // Create user transaction statistics from transaction list
    private UserTransactionStats createUserTransactionStats(
        List<TransactionHistory> transactions,
        Instant startDate,
        Instant endDate
    ) {
        UserTransactionStats stats = new UserTransactionStats();

        // Basic counts
        int total = transactions.size();
        stats.setTotalTransactions(total);

        // Amount calculations
        BigDecimal totalAmount = transactions.stream()
            .map(TransactionHistory::getAmount)
            .filter(Objects::nonNull)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.setTotalAmount(totalAmount);

        // Status counts
        stats.setSuccessfulTransactions((int) transactions.stream()
            .filter(t -> t.getStatus() == TransactionStatus.SUCCESS)
            .count());
        stats.setFailedTransactions((int) transactions.stream()
            .filter(t -> t.getStatus() == TransactionStatus.FAILED)
            .count());
        stats.setPendingTransactions((int) transactions.stream()
            .filter(t -> t.getStatus() == TransactionStatus.PENDING)
            .count());
        stats.setCancelledTransactions((int) transactions.stream()
            .filter(t -> t.getStatus() == TransactionStatus.CANCELLED)
            .count());
        stats.setProcessingTransactions((int) transactions.stream()
            .filter(t -> t.getStatus() == TransactionStatus.PROCESSING)
            .count());

        // Transaction type summary
        Map<TransactionType, List<TransactionHistory>> transactionsByType = transactions.stream()
            .collect(Collectors.groupingBy(TransactionHistory::getType));

        List<TransactionTypeSummary> typeSummaries = transactionsByType.entrySet().stream()
            .map(entry -> {
                TransactionType type = entry.getKey();
                List<TransactionHistory> typeTransactions = entry.getValue();

                BigDecimal typeTotalAmount = typeTransactions.stream()
                    .map(TransactionHistory::getAmount)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

                float percentage = total > 0 ? (typeTransactions.size() * 100.0f) / total : 0;

                return new TransactionTypeSummary(
                    type,
                    typeTransactions.size(),
                    typeTotalAmount,
                    percentage
                );
            })
            .sorted((a, b) -> b.getCount() - a.getCount()) // Sort by count descending
            .collect(Collectors.toList());

        stats.setTransactionTypeSummary(typeSummaries);

        // Date information
        Instant firstTransactionDate = transactions.stream()
            .map(TransactionHistory::getTransactionDate)
            .filter(Objects::nonNull)
            .min(Instant::compareTo)
            .orElse(null);
        stats.setFirstTransactionDate(firstTransactionDate);

        Instant lastTransactionDate = transactions.stream()
            .map(TransactionHistory::getTransactionDate)
            .filter(Objects::nonNull)
            .max(Instant::compareTo)
            .orElse(null);
        stats.setLastTransactionDate(lastTransactionDate);

        stats.setPeriodStart(startDate);
        stats.setPeriodEnd(endDate);

        // Monthly summary
        if (startDate != null && endDate != null) {
            List<MonthlySummary> monthlySummaries = calculateMonthlySummaries(transactions, startDate, endDate);
            stats.setMonthlySummary(monthlySummaries);
        }

        return stats;
    }

    private List<MonthlySummary> calculateMonthlySummaries(
        List<TransactionHistory> transactions,
        Instant startDate,
        Instant endDate
    ) {
        // Group transactions by month
        Map<String, List<TransactionHistory>> transactionsByMonth = transactions.stream()
            .collect(Collectors.groupingBy(t -> {
                Instant date = t.getTransactionDate();
                if (date == null) return "unknown";
                YearMonth yearMonth = YearMonth.from(date.atZone(java.time.ZoneId.systemDefault()));
                return yearMonth.format(DateTimeFormatter.ofPattern("yyyy-MM"));
            }));

        return transactionsByMonth.entrySet().stream()
            .map(entry -> {
                String month = entry.getKey();
                List<TransactionHistory> monthTransactions = entry.getValue();

                BigDecimal monthTotalAmount = monthTransactions.stream()
                    .map(TransactionHistory::getAmount)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

                return new MonthlySummary(
                    month,
                    monthTransactions.size(),
                    monthTotalAmount
                );
            })
            .sorted(Comparator.comparing(MonthlySummary::getMonth))
            .collect(Collectors.toList());
    }

    private UserTransactionStats createEmptyStats(Instant startDate, Instant endDate) {
        UserTransactionStats stats = new UserTransactionStats();
        stats.setTotalTransactions(0);
        stats.setTotalAmount(BigDecimal.ZERO);
        stats.setSuccessfulTransactions(0);
        stats.setFailedTransactions(0);
        stats.setPendingTransactions(0);
        stats.setCancelledTransactions(0);
        stats.setProcessingTransactions(0);
        stats.setTransactionTypeSummary(Collections.emptyList());
        stats.setFirstTransactionDate(null);
        stats.setLastTransactionDate(null);
        stats.setPeriodStart(startDate);
        stats.setPeriodEnd(endDate);
        stats.setMonthlySummary(Collections.emptyList());
        return stats;
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
        List<TransactionType> types = type != null ? List.of(type) : null;
        List<TransactionStatus> statuses = status != null ? List.of(status) : null;

        return advancedSearch(
            senderPhone,
            receiverPhone,
            types,
            statuses,
            startDate,
            endDate,
            minAmount,
            maxAmount,
            null, // currency
            null, // direction
            null, // merchantCode
            null, // billReference
            null, // bankAccountNumber
            null, // descriptionContains
            pageable
        );
    }
}

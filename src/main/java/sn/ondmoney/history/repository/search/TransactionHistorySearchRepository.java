package sn.ondmoney.history.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import sn.ondmoney.history.domain.TransactionHistory;
import sn.ondmoney.history.domain.enumeration.*;
import sn.ondmoney.history.repository.TransactionHistoryRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;
import sn.ondmoney.history.service.PhoneNumberNormalizer;

/**
 * Spring Data Elasticsearch repository for the {@link TransactionHistory} entity.
 */
public interface TransactionHistorySearchRepository
    extends ElasticsearchRepository<TransactionHistory, String>, TransactionHistorySearchRepositoryInternal {}

interface TransactionHistorySearchRepositoryInternal {
    Page<TransactionHistory> search(String query, Pageable pageable);

    Page<TransactionHistory> search(Query query);

    // Existing methods with QUERYSTRING
    Page<TransactionHistory> findBySenderPhone(String senderPhone, Pageable pageable);
    Page<TransactionHistory> findByReceiverPhone(String receiverPhone, Pageable pageable);
    Page<TransactionHistory> findByTransactionDateBetween(Instant startDate, Instant endDate, Pageable pageable);
    Page<TransactionHistory> findByAmountBetween(BigDecimal minAmount, BigDecimal maxAmount, Pageable pageable);
    Page<TransactionHistory> findByTypeAndStatus(String type, String status, Pageable pageable);

    // NEW: Find by user phone (both sender and receiver)
    Page<TransactionHistory> findByUserPhone(String phoneNumber, Pageable pageable);

    // Original advanced search method for backward compatibility
    Page<TransactionHistory> searchByCriteria(
        String senderPhone,
        String receiverPhone,
        TransactionType transactionType,
        TransactionStatus transactionStatus,
        Instant startDate,
        Instant endDate,
        BigDecimal minAmount,
        BigDecimal maxAmount,
        Pageable pageable
    );

    // NEW: Enhanced search with all criteria
    Page<TransactionHistory> searchByCriteria(
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
    );

    @Async
    void index(TransactionHistory entity);

    @Async
    void deleteFromIndexById(String id);
}

class TransactionHistorySearchRepositoryInternalImpl implements TransactionHistorySearchRepositoryInternal {

    private static final Logger LOG = LoggerFactory.getLogger(TransactionHistorySearchRepositoryInternalImpl.class);
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_INSTANT;

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final TransactionHistoryRepository repository;
    private final PhoneNumberNormalizer phoneNormalizer;

    TransactionHistorySearchRepositoryInternalImpl(
        ElasticsearchTemplate elasticsearchTemplate,
        TransactionHistoryRepository repository,
        PhoneNumberNormalizer phoneNormalizer
    ) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
        this.phoneNormalizer = phoneNormalizer;
    }

    @Override
    public Page<TransactionHistory> search(String query, Pageable pageable) {
        LOG.debug("Executing Elasticsearch QueryString: {}", query);
        try {
            NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
            return search(nativeQuery.setPageable(pageable));
        } catch (Exception e) {
            LOG.error("Error executing QueryString query: {}", query, e);
            throw new RuntimeException("Elasticsearch query failed: " + e.getMessage(), e);
        }
    }

    @Override
    public Page<TransactionHistory> search(Query query) {
        SearchHits<TransactionHistory> searchHits = elasticsearchTemplate.search(query, TransactionHistory.class);
        List<TransactionHistory> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public Page<TransactionHistory> findBySenderPhone(String senderPhone, Pageable pageable) {
        if (senderPhone == null || senderPhone.isBlank()) {
            return search("*", pageable);
        }

        String normalized = phoneNormalizer.normalize(senderPhone);

        // Recherche flexible : match 00221… ou 0…
        String altNormalized = normalized.startsWith("00221")
            ? "0" + normalized.substring(5)
            : normalized;

        String query = "senderPhone:(\"" + normalized + "\" OR \"" + altNormalized + "\")";

        return search(query, pageable);
    }

    @Override
    public Page<TransactionHistory> findByReceiverPhone(String receiverPhone, Pageable pageable) {
        if (receiverPhone == null || receiverPhone.isBlank()) {
            return search("*", pageable);
        }

        // Normalisation simple
        String normalized = phoneNormalizer.normalize(receiverPhone);
        String query = "receiverPhone:\"" + escapeQueryString(normalized) + "\"";
        return search(query, pageable);
    }

    @Override
    public Page<TransactionHistory> findByUserPhone(String phoneNumber, Pageable pageable) {
        if (phoneNumber == null || phoneNumber.isBlank()) {
            return search("*", pageable);
        }

        // Normalisation simple
        String normalized = phoneNormalizer.normalize(phoneNumber);
        String query = "(senderPhone:\"" + escapeQueryString(normalized) +
            "\" OR receiverPhone:\"" + escapeQueryString(normalized) + "\")";
        return search(query, pageable);
    }

    @Override
    public Page<TransactionHistory> findByTransactionDateBetween(Instant startDate, Instant endDate, Pageable pageable) {
        String queryString = String.format("transactionDate:[%s TO %s]",
            formatDateForQuery(startDate),
            formatDateForQuery(endDate)
        );
        return search(queryString, pageable);
    }

    @Override
    public Page<TransactionHistory> findByAmountBetween(BigDecimal minAmount, BigDecimal maxAmount, Pageable pageable) {
        String queryString = String.format("amount:[%s TO %s]",
            minAmount.floatValue(),
            maxAmount.floatValue()
        );
        return search(queryString, pageable);
    }

    @Override
    public Page<TransactionHistory> findByTypeAndStatus(String type, String status, Pageable pageable) {
        String queryString = String.format("type:%s AND status:%s", type, status);
        return search(queryString, pageable);
    }

    @Override
    public Page<TransactionHistory> searchByCriteria(
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
        // For backward compatibility - convert single type/status to lists
        List<TransactionType> types = type != null ? List.of(type) : null;
        List<TransactionStatus> statuses = status != null ? List.of(status) : null;

        return searchByCriteria(
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

    @Override
    public Page<TransactionHistory> searchByCriteria(
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
        // Build query string
        StringBuilder queryString = new StringBuilder();

        // Handle phone filters based on direction
        handlePhoneFilters(queryString, senderPhone, receiverPhone, direction);

        // Add type filters
        addListFilter(queryString, "type", types);

        // Add status filters
        addListFilter(queryString, "status", statuses);

        // Add date range
        addDateRangeFilter(queryString, startDate, endDate);

        // Add amount range
        addAmountRangeFilter(queryString, minAmount, maxAmount);

        // Add other filters
        addTextFieldFilter(queryString, "currency", currency);
        addTextFieldFilter(queryString, "merchantCode", merchantCode);
        addTextFieldFilter(queryString, "billReference", billReference);
        addTextFieldFilter(queryString, "bankAccountNumber", bankAccountNumber);

        // Add description wildcard search
        if (descriptionContains != null && !descriptionContains.trim().isEmpty()) {
            addAndClause(queryString);
            queryString.append("description:*").append(escapeQueryString(descriptionContains)).append("*");
        }

        String finalQuery = queryString.toString();
        if (finalQuery.isEmpty()) {
            finalQuery = "*";
        }

        LOG.debug("Elasticsearch Query: {}", finalQuery);
        return search(finalQuery, pageable);
    }

    // ========== HELPER METHODS ==========

    /**
     * Search by a single field
     */
    private Page<TransactionHistory> searchByField(String fieldName, String value, Pageable pageable) {
        if (value == null || value.trim().isEmpty()) {
            return search("*", pageable);
        }

        String queryString = String.format("%s:\"%s\"", fieldName, escapeQueryString(value));
        return search(queryString, pageable);
    }

    /**
     * Handle phone number filters based on direction
     */
    private void handlePhoneFilters(StringBuilder queryString,
                                    String senderPhone,
                                    String receiverPhone,
                                    TransactionDirection direction) {
        if (direction != null) {
            switch (direction) {
                case SENT:
                    addPhoneFilter(queryString, "senderPhone", senderPhone);
                    break;
                case RECEIVED:
                    addPhoneFilter(queryString, "receiverPhone", receiverPhone);
                    break;
                case ALL:
                    String phoneToSearch = getFirstNonNull(senderPhone, receiverPhone);
                    if (phoneToSearch != null) {
                        addAndClause(queryString);
                        String normalizedPhone = normalizePhoneNumber(phoneToSearch);
                        queryString.append(String.format("(senderPhone:\"%s\" OR receiverPhone:\"%s\")",
                            escapeQueryString(normalizedPhone),
                            escapeQueryString(normalizedPhone)
                        ));
                    }
                    break;
            }
        } else {
            // No direction specified
            addPhoneFilter(queryString, "senderPhone", senderPhone);
            addPhoneFilter(queryString, "receiverPhone", receiverPhone);
        }
    }

    /**
     * Add phone filter if phone is not null/empty
     */
    private void addPhoneFilter(StringBuilder queryString, String fieldName, String phone) {
        if (phone != null && !phone.trim().isEmpty()) {
            addAndClause(queryString);
            String normalizedPhone = normalizePhoneNumber(phone);
            queryString.append(String.format("%s:\"%s\"",
                fieldName,
                escapeQueryString(normalizedPhone)
            ));
        }
    }

    /**
     * Add list filter (for types, statuses)
     */
    private <T extends Enum<T>> void addListFilter(StringBuilder queryString,
                                                   String fieldName,
                                                   List<T> values) {
        if (values != null && !values.isEmpty()) {
            addAndClause(queryString);
            queryString.append("(");
            for (int i = 0; i < values.size(); i++) {
                if (i > 0) queryString.append(" OR ");
                queryString.append(fieldName).append(":").append(values.get(i));
            }
            queryString.append(")");
        }
    }

    /**
     * Add date range filter
     */
    private void addDateRangeFilter(StringBuilder queryString,
                                    Instant startDate,
                                    Instant endDate) {
        if (startDate != null && endDate != null) {
            addAndClause(queryString);
            queryString.append(String.format("transactionDate:[%s TO %s]",
                formatDateForQuery(startDate),
                formatDateForQuery(endDate)
            ));
        } else if (startDate != null) {
            addAndClause(queryString);
            queryString.append(String.format("transactionDate:[%s TO *]",
                formatDateForQuery(startDate)
            ));
        } else if (endDate != null) {
            addAndClause(queryString);
            queryString.append(String.format("transactionDate:[* TO %s]",
                formatDateForQuery(endDate)
            ));
        }
    }

    /**
     * Add amount range filter
     */
    private void addAmountRangeFilter(StringBuilder queryString,
                                      BigDecimal minAmount,
                                      BigDecimal maxAmount) {
        if (minAmount != null && maxAmount != null) {
            addAndClause(queryString);
            queryString.append(String.format("amount:[%s TO %s]",
                minAmount.floatValue(),
                maxAmount.floatValue()
            ));
        } else if (minAmount != null) {
            addAndClause(queryString);
            queryString.append(String.format("amount:[%s TO *]",
                minAmount.floatValue()
            ));
        } else if (maxAmount != null) {
            addAndClause(queryString);
            queryString.append(String.format("amount:[* TO %s]",
                maxAmount.floatValue()
            ));
        }
    }

    /**
     * Add text field filter (for currency, merchantCode, etc.)
     */
    private void addTextFieldFilter(StringBuilder queryString,
                                    String fieldName,
                                    String value) {
        if (value != null && !value.trim().isEmpty()) {
            addAndClause(queryString);
            queryString.append(String.format("%s:\"%s\"",
                fieldName,
                escapeQueryString(value)
            ));
        }
    }

    /**
     * Normalize phone number (convert +221 to 00221)
     */
    private String normalizePhoneNumber(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return phone;
        }

        String cleaned = phone.trim();

        if (cleaned.startsWith("+221")) {
            return "00221" + cleaned.substring(4);
        }

        if (cleaned.startsWith("221") && !cleaned.startsWith("00221")) {
            return "00" + cleaned;
        }

        return cleaned;
    }

    /**
     * Escape special characters for QueryString
     */
    private String escapeQueryString(String input) {
        if (input == null) return "";
        return input.replaceAll("([+\\-=&|><!(){}\\[\\]^\"~*?:\\\\/])", "\\\\$1");
    }

    /**
     * Format date for QueryString
     */
    private String formatDateForQuery(Instant date) {
        return date != null ? date.toString() : "";
    }

    /**
     * Get first non-null value
     */
    private String getFirstNonNull(String... values) {
        for (String value : values) {
            if (value != null && !value.trim().isEmpty()) {
                return value;
            }
        }
        return null;
    }

    /**
     * Add AND clause if needed
     */
    private void addAndClause(StringBuilder queryString) {
        if (queryString.length() > 0) {
            queryString.append(" AND ");
        }
    }

    @Override
    public void index(TransactionHistory entity) {
        try {
            repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
        } catch (Exception e) {
            LOG.error("Failed to index transaction: {}", entity.getId(), e);
        }
    }

    @Override
    public void deleteFromIndexById(String id) {
        try {
            elasticsearchTemplate.delete(String.valueOf(id), TransactionHistory.class);
        } catch (Exception e) {
            LOG.error("Failed to delete from index: {}", id, e);
        }
    }
}

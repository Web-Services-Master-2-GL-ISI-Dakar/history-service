package sn.ondmoney.history.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import sn.ondmoney.history.domain.TransactionHistory;
import sn.ondmoney.history.domain.enumeration.*;
import sn.ondmoney.history.repository.TransactionHistoryRepository;
import java.math.BigDecimal;
import java.time.Instant;
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

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final TransactionHistoryRepository repository;

    TransactionHistorySearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, TransactionHistoryRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<TransactionHistory> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<TransactionHistory> search(Query query) {
        SearchHits<TransactionHistory> searchHits = elasticsearchTemplate.search(query, TransactionHistory.class);
        List<TransactionHistory> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public Page<TransactionHistory> findBySenderPhone(String senderPhone, Pageable pageable) {
        String queryString = "senderPhone:" + senderPhone;
        return search(queryString, pageable);
    }

    @Override
    public Page<TransactionHistory> findByReceiverPhone(String receiverPhone, Pageable pageable) {
        String queryString = "receiverPhone:" + receiverPhone;
        return search(queryString, pageable);
    }

    @Override
    public Page<TransactionHistory> findByUserPhone(String phoneNumber, Pageable pageable) {
        String queryString = "(senderPhone:" + phoneNumber + " OR receiverPhone:" + phoneNumber + ")";
        return search(queryString, pageable);
    }

    @Override
    public Page<TransactionHistory> findByTransactionDateBetween(Instant startDate, Instant endDate, Pageable pageable) {
        String queryString = "transactionDate:[" + startDate + " TO " + endDate + "]";
        return search(queryString, pageable);
    }

    @Override
    public Page<TransactionHistory> findByAmountBetween(BigDecimal minAmount, BigDecimal maxAmount, Pageable pageable) {
        String queryString = "amount:[" + minAmount + " TO " + maxAmount + "]";
        return search(queryString, pageable);
    }

    @Override
    public Page<TransactionHistory> findByTypeAndStatus(String type, String status, Pageable pageable) {
        String queryString = "type:" + type + " AND status:" + status;
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
        // Build query string with all criteria
        StringBuilder queryString = new StringBuilder();

        // Handle direction-based phone filtering
        if (direction != null) {
            switch (direction) {
                case SENT:
                    if (senderPhone != null) {
                        addAndClause(queryString);
                        queryString.append("senderPhone:").append(senderPhone);
                    }
                    break;
                case RECEIVED:
                    if (receiverPhone != null) {
                        addAndClause(queryString);
                        queryString.append("receiverPhone:").append(receiverPhone);
                    }
                    break;
                case ALL:
                    // For ALL direction, search both sender and receiver
                    if (senderPhone != null && receiverPhone != null) {
                        addAndClause(queryString);
                        queryString.append("(senderPhone:").append(senderPhone)
                            .append(" OR receiverPhone:").append(receiverPhone).append(")");
                    } else if (senderPhone != null) {
                        addAndClause(queryString);
                        queryString.append("(senderPhone:").append(senderPhone)
                            .append(" OR receiverPhone:").append(senderPhone).append(")");
                    } else if (receiverPhone != null) {
                        addAndClause(queryString);
                        queryString.append("(senderPhone:").append(receiverPhone)
                            .append(" OR receiverPhone:").append(receiverPhone).append(")");
                    }
                    break;
            }
        } else {
            // No direction specified, use individual phone filters
            if (senderPhone != null) {
                addAndClause(queryString);
                queryString.append("senderPhone:").append(senderPhone);
            }
            if (receiverPhone != null) {
                addAndClause(queryString);
                queryString.append("receiverPhone:").append(receiverPhone);
            }
        }

        // Multiple types
        if (types != null && !types.isEmpty()) {
            addAndClause(queryString);
            queryString.append("(");
            for (int i = 0; i < types.size(); i++) {
                if (i > 0) queryString.append(" OR ");
                queryString.append("type:").append(types.get(i));
            }
            queryString.append(")");
        }

        // Multiple statuses
        if (statuses != null && !statuses.isEmpty()) {
            addAndClause(queryString);
            queryString.append("(");
            for (int i = 0; i < statuses.size(); i++) {
                if (i > 0) queryString.append(" OR ");
                queryString.append("status:").append(statuses.get(i));
            }
            queryString.append(")");
        }

        // Date range
        if (startDate != null && endDate != null) {
            addAndClause(queryString);
            queryString.append("transactionDate:[").append(startDate).append(" TO ").append(endDate).append("]");
        } else if (startDate != null) {
            addAndClause(queryString);
            queryString.append("transactionDate:[").append(startDate).append(" TO *]");
        } else if (endDate != null) {
            addAndClause(queryString);
            queryString.append("transactionDate:[* TO ").append(endDate).append("]");
        }

        // Amount range
        if (minAmount != null && maxAmount != null) {
            addAndClause(queryString);
            queryString.append("amount:[").append(minAmount).append(" TO ").append(maxAmount).append("]");
        } else if (minAmount != null) {
            addAndClause(queryString);
            queryString.append("amount:[").append(minAmount).append(" TO *]");
        } else if (maxAmount != null) {
            addAndClause(queryString);
            queryString.append("amount:[* TO ").append(maxAmount).append("]");
        }

        // Currency
        if (currency != null && !currency.trim().isEmpty()) {
            addAndClause(queryString);
            queryString.append("currency:").append(currency);
        }

        // Merchant code
        if (merchantCode != null && !merchantCode.trim().isEmpty()) {
            addAndClause(queryString);
            queryString.append("merchantCode:").append(merchantCode);
        }

        // Bill reference
        if (billReference != null && !billReference.trim().isEmpty()) {
            addAndClause(queryString);
            queryString.append("billReference:").append(billReference);
        }

        // Bank account number
        if (bankAccountNumber != null && !bankAccountNumber.trim().isEmpty()) {
            addAndClause(queryString);
            queryString.append("bankAccountNumber:").append(bankAccountNumber);
        }

        // Description contains (wildcard search)
        if (descriptionContains != null && !descriptionContains.trim().isEmpty()) {
            addAndClause(queryString);
            queryString.append("description:*").append(descriptionContains).append("*");
        }

        String finalQuery = queryString.toString();

        // If no criteria specified, return all results
        if (finalQuery.isEmpty()) {
            finalQuery = "*";
        }

        LOG.debug("Executing enhanced QUERYSTRING search: {}", finalQuery);
        return search(finalQuery, pageable);
    }

    private void addAndClause(StringBuilder queryString) {
        if (queryString.length() > 0) {
            queryString.append(" AND ");
        }
    }

    @Override
    public void index(TransactionHistory entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(String id) {
        elasticsearchTemplate.delete(String.valueOf(id), TransactionHistory.class);
    }
}

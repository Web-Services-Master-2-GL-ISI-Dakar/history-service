package com.ondmoney.sn.history.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.ondmoney.sn.history.domain.TransactionHistory;
import com.ondmoney.sn.history.domain.enumeration.TransactionStatus;
import com.ondmoney.sn.history.domain.enumeration.TransactionType;
import com.ondmoney.sn.history.repository.TransactionHistoryRepository;
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

    // Méthodes de recherche existantes avec QUERYSTRING
    Page<TransactionHistory> findBySenderPhone(String senderPhone, Pageable pageable);
    Page<TransactionHistory> findByReceiverPhone(String receiverPhone, Pageable pageable);
    Page<TransactionHistory> findByTransactionDateBetween(Instant startDate, Instant endDate, Pageable pageable);
    Page<TransactionHistory> findByAmountBetween(BigDecimal minAmount, BigDecimal maxAmount, Pageable pageable);
    Page<TransactionHistory> findByTypeAndStatus(String type, String status, Pageable pageable);

    // Méthode de recherche avancée avec QUERYSTRING
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
        // Construction de la requête QUERYSTRING comme la recherche originale
        StringBuilder queryString = new StringBuilder();

        // Filtre par expéditeur
        if (senderPhone != null && !senderPhone.trim().isEmpty()) {
<<<<<<< HEAD
            if (!queryString.isEmpty()) queryString.append(" AND ");
=======
            if (queryString.length() > 0) queryString.append(" AND ");
>>>>>>> 74113b81d551b4b03d07d72216f57015d3781672
            queryString.append("senderPhone:").append(senderPhone);
        }

        // Filtre par destinataire
        if (receiverPhone != null && !receiverPhone.trim().isEmpty()) {
<<<<<<< HEAD
            if (!queryString.isEmpty()) queryString.append(" AND ");
=======
            if (queryString.length() > 0) queryString.append(" AND ");
>>>>>>> 74113b81d551b4b03d07d72216f57015d3781672
            queryString.append("receiverPhone:").append(receiverPhone);
        }

        // Filtre par type
        if (type != null && !type.name().trim().isEmpty()) {
<<<<<<< HEAD
            if (!queryString.isEmpty()) queryString.append(" AND ");
=======
            if (queryString.length() > 0) queryString.append(" AND ");
>>>>>>> 74113b81d551b4b03d07d72216f57015d3781672
            queryString.append("type:").append(type);
        }

        // Filtre par statut
        if (status != null && !status.name().trim().isEmpty()) {
<<<<<<< HEAD
            if (!queryString.isEmpty()) queryString.append(" AND ");
=======
            if (queryString.length() > 0) queryString.append(" AND ");
>>>>>>> 74113b81d551b4b03d07d72216f57015d3781672
            queryString.append("status:").append(status);
        }

        // Filtre par plage de dates
        if (startDate != null && endDate != null) {
<<<<<<< HEAD
            if (!queryString.isEmpty()) queryString.append(" AND ");
=======
            if (queryString.length() > 0) queryString.append(" AND ");
>>>>>>> 74113b81d551b4b03d07d72216f57015d3781672
            queryString.append("transactionDate:[").append(startDate).append(" TO ").append(endDate).append("]");
        }

        // Filtre par plage de montants
        if (minAmount != null && maxAmount != null) {
<<<<<<< HEAD
            if (!queryString.isEmpty()) queryString.append(" AND ");
=======
            if (queryString.length() > 0) queryString.append(" AND ");
>>>>>>> 74113b81d551b4b03d07d72216f57015d3781672
            queryString.append("amount:[").append(minAmount).append(" TO ").append(maxAmount).append("]");
        }

        String finalQuery = queryString.toString();

        // Si aucun critère n'est spécifié, on retourne tous les résultats
        if (finalQuery.isEmpty()) {
            finalQuery = "*";
        }

        LOG.debug("Executing QUERYSTRING search: {}", finalQuery);
        return search(finalQuery, pageable);
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

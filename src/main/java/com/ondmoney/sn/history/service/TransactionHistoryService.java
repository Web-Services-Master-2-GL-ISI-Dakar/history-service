package com.ondmoney.sn.history.service;

import co.elastic.clients.elasticsearch._types.query_dsl.*;
import com.ondmoney.sn.history.domain.TransactionHistory;
import com.ondmoney.sn.history.domain.enumeration.TransactionStatus;
import com.ondmoney.sn.history.domain.enumeration.TransactionType;
import com.ondmoney.sn.history.repository.TransactionHistoryRepository;
import com.ondmoney.sn.history.repository.search.TransactionHistorySearchRepository;
import com.ondmoney.sn.history.service.dto.TransactionHistoryDTO;
import com.ondmoney.sn.history.service.mapper.TransactionHistoryMapper;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

/**
 * Service Implementation for managing {@link com.ondmoney.sn.history.domain.TransactionHistory}.
 */
@Service
public class TransactionHistoryService {

    private static final Logger LOG = LoggerFactory.getLogger(TransactionHistoryService.class);

    private final TransactionHistoryRepository transactionHistoryRepository;
    private final TransactionHistoryMapper transactionHistoryMapper;
    private final TransactionHistorySearchRepository transactionHistorySearchRepository;

    public TransactionHistoryService(
        TransactionHistoryRepository transactionHistoryRepository,
        TransactionHistoryMapper transactionHistoryMapper,
        TransactionHistorySearchRepository transactionHistorySearchRepository
    ) {
        this.transactionHistoryRepository = transactionHistoryRepository;
        this.transactionHistoryMapper = transactionHistoryMapper;
        this.transactionHistorySearchRepository = transactionHistorySearchRepository;
    }

    /**
     * Search transactions with multiple criteria using the existing search repository pattern
     */
    public Page<TransactionHistoryDTO> searchByCriteria(
        String senderPhone,
        String receiverPhone,
        TransactionType transactionType,
        TransactionStatus transactionStatus,
        Instant startDate,
        Instant endDate,
        BigDecimal minAmount,
        BigDecimal maxAmount,
        Pageable pageable
    ) {
        LOG.debug(
            "Searching transactions with criteria - sender: {}, receiver: {}, type: {}, status: {}, dateRange: {}-{}, amountRange: {}-{}",
            senderPhone,
            receiverPhone,
            transactionType,
            transactionStatus,
            startDate,
            endDate,
            minAmount,
            maxAmount
        );

        Page<TransactionHistory> page = transactionHistorySearchRepository.searchByCriteria(
            senderPhone,
            receiverPhone,
            transactionType,
            transactionStatus,
            startDate,
            endDate,
            minAmount,
            maxAmount,
            pageable
        );

        return page.map(transactionHistoryMapper::toDto);
    }

    /**
     * Save a transactionHistory.
     *
     * @param transactionHistoryDTO the entity to save.
     * @return the persisted entity.
     */
    public TransactionHistoryDTO save(TransactionHistoryDTO transactionHistoryDTO) {
        LOG.debug("Request to save TransactionHistory : {}", transactionHistoryDTO);
        TransactionHistory transactionHistory = transactionHistoryMapper.toEntity(transactionHistoryDTO);
        transactionHistory = transactionHistoryRepository.save(transactionHistory);
        transactionHistorySearchRepository.index(transactionHistory);
        return transactionHistoryMapper.toDto(transactionHistory);
    }

    /**
     * Update a transactionHistory.
     *
     * @param transactionHistoryDTO the entity to save.
     * @return the persisted entity.
     */
    public TransactionHistoryDTO update(TransactionHistoryDTO transactionHistoryDTO) {
        LOG.debug("Request to update TransactionHistory : {}", transactionHistoryDTO);
        TransactionHistory transactionHistory = transactionHistoryMapper.toEntity(transactionHistoryDTO);
        transactionHistory = transactionHistoryRepository.save(transactionHistory);
        transactionHistorySearchRepository.index(transactionHistory);
        return transactionHistoryMapper.toDto(transactionHistory);
    }

    /**
     * Partially update a transactionHistory.
     *
     * @param transactionHistoryDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<TransactionHistoryDTO> partialUpdate(TransactionHistoryDTO transactionHistoryDTO) {
        LOG.debug("Request to partially update TransactionHistory : {}", transactionHistoryDTO);

        return transactionHistoryRepository
            .findById(transactionHistoryDTO.getId())
            .map(existingTransactionHistory -> {
                transactionHistoryMapper.partialUpdate(existingTransactionHistory, transactionHistoryDTO);

                return existingTransactionHistory;
            })
            .map(transactionHistoryRepository::save)
            .map(savedTransactionHistory -> {
                transactionHistorySearchRepository.index(savedTransactionHistory);
                return savedTransactionHistory;
            })
            .map(transactionHistoryMapper::toDto);
    }

    /**
     * Get all the transactionHistories.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    public Page<TransactionHistoryDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all TransactionHistories");
        return transactionHistoryRepository.findAll(pageable).map(transactionHistoryMapper::toDto);
    }

    /**
     * Get one transactionHistory by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    public Optional<TransactionHistoryDTO> findOne(String id) {
        LOG.debug("Request to get TransactionHistory : {}", id);
        return transactionHistoryRepository.findById(id).map(transactionHistoryMapper::toDto);
    }

    /**
     * Delete the transactionHistory by id.
     *
     * @param id the id of the entity.
     */
    public void delete(String id) {
        LOG.debug("Request to delete TransactionHistory : {}", id);
        transactionHistoryRepository.deleteById(id);
        transactionHistorySearchRepository.deleteFromIndexById(id);
    }

    /**
     * Search for the transactionHistory corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    public Page<TransactionHistoryDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of TransactionHistories for query {}", query);
        return transactionHistorySearchRepository.search(query, pageable).map(transactionHistoryMapper::toDto);
    }
}

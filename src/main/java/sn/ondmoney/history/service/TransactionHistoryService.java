package sn.ondmoney.history.service;

import sn.ondmoney.history.domain.TransactionHistory;
import sn.ondmoney.history.domain.enumeration.*;
import sn.ondmoney.history.repository.TransactionHistoryRepository;
import sn.ondmoney.history.repository.search.TransactionHistorySearchRepository;
import sn.ondmoney.history.service.dto.TransactionHistoryDTO;
import sn.ondmoney.history.service.mapper.TransactionHistoryMapper;
import sn.ondmoney.history.web.graphql.response.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Service Implementation for managing {@link TransactionHistory}.
 */
@Service
@Transactional
public class TransactionHistoryService {

    private static final Logger LOG = LoggerFactory.getLogger(TransactionHistoryService.class);

    private final TransactionHistoryRepository transactionHistoryRepository;
    private final TransactionHistoryMapper transactionHistoryMapper;
    private final TransactionHistorySearchRepository transactionHistorySearchRepository;
    private final TransactionHistorySearchService transactionHistorySearchService;

    private final PhoneNumberNormalizer phoneNumberNormalizer;

    public TransactionHistoryService(
        TransactionHistoryRepository transactionHistoryRepository,
        TransactionHistoryMapper transactionHistoryMapper,
        TransactionHistorySearchRepository transactionHistorySearchRepository,
        TransactionHistorySearchService transactionHistorySearchService,
        PhoneNumberNormalizer phoneNumberNormalizer
    ) {
        this.phoneNumberNormalizer = phoneNumberNormalizer;
        this.transactionHistoryRepository = transactionHistoryRepository;
        this.transactionHistoryMapper = transactionHistoryMapper;
        this.transactionHistorySearchRepository = transactionHistorySearchRepository;
        this.transactionHistorySearchService = transactionHistorySearchService;
    }

    /**
     * Search transactions with multiple criteria using the enhanced search service
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

        // Convert single type/status to lists for backward compatibility
        List<TransactionType> types = transactionType != null ? List.of(transactionType) : null;
        List<TransactionStatus> statuses = transactionStatus != null ? List.of(transactionStatus) : null;

        Page<TransactionHistory> page = transactionHistorySearchService.advancedSearch(
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

        return page.map(transactionHistoryMapper::toDto);
    }

    /**
     * Enhanced search with all new criteria
     */
    public Page<TransactionHistoryDTO> searchByCriteria(
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
            "Enhanced search with criteria - sender: {}, receiver: {}, types: {}, statuses: {}, dateRange: {}-{}, amountRange: {}-{}, currency: {}, direction: {}, merchantCode: {}, billRef: {}, bankAcc: {}, descriptionContains: {}",
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

        Page<TransactionHistory> page = transactionHistorySearchService.advancedSearch(
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
            descriptionContains,
            pageable
        );

        return page.map(transactionHistoryMapper::toDto);
    }

    /**
     * Get user transaction statistics
     */
    public UserTransactionStats getUserTransactionStats(
        String phoneNumber,
        Instant startDate,
        Instant endDate,
        List<TransactionType> types,
        TransactionDirection direction
    ) {
        LOG.debug("Getting transaction stats for user: {}", phoneNumber);
        return transactionHistorySearchService.getUserTransactionStats(
            phoneNumber,
            startDate,
            endDate,
            types,
            direction
        );
    }

    /**
     * Get all transactions for a user (as sender or receiver)
     */
    public Page<TransactionHistoryDTO> getUserTransactions(
        String phoneNumber,
        Pageable pageable
    ) {
        LOG.debug("Getting all transactions for user: {}", phoneNumber);
        Page<TransactionHistory> page = transactionHistorySearchRepository.findByUserPhone(phoneNumber, pageable);
        return page.map(transactionHistoryMapper::toDto);
    }

    /**
     * Get transactions where user is sender
     */
    public Page<TransactionHistoryDTO> getSentTransactions(
        String phoneNumber,
        Pageable pageable
    ) {
        LOG.debug("Getting sent transactions for user: {}", phoneNumber);
        Page<TransactionHistory> page = transactionHistorySearchRepository.findBySenderPhone(phoneNumber, pageable);
        return page.map(transactionHistoryMapper::toDto);
    }

    /**
     * Get transactions where user is receiver
     */
    public Page<TransactionHistoryDTO> getReceivedTransactions(
        String phoneNumber,
        Pageable pageable
    ) {
        LOG.debug("Getting received transactions for user: {}", phoneNumber);
        Page<TransactionHistory> page = transactionHistorySearchRepository.findByReceiverPhone(phoneNumber, pageable);
        return page.map(transactionHistoryMapper::toDto);
    }

    private void normalizePhoneNumbers(TransactionHistoryDTO dto) {
        if (dto.getSenderPhone() != null) {
            dto.setSenderPhone(phoneNumberNormalizer.normalize(dto.getSenderPhone()));
        }
        if (dto.getReceiverPhone() != null) {
            dto.setReceiverPhone(phoneNumberNormalizer.normalize(dto.getReceiverPhone()));
        }
    }

    /**
     * Save a transactionHistory.
     *
     * @param transactionHistoryDTO the entity to save.
     * @return the persisted entity.
     */
    public TransactionHistoryDTO save(TransactionHistoryDTO transactionHistoryDTO) {
        LOG.debug("Request to save TransactionHistory: {}", transactionHistoryDTO);

        if (transactionHistoryDTO.getId() != null) {
            throw new RuntimeException("A new transactionHistory cannot already have an ID");
        }

        // Normalisation simple
        normalizePhoneNumbers(transactionHistoryDTO);

        TransactionHistory entity = transactionHistoryMapper.toEntity(transactionHistoryDTO);
        entity = transactionHistoryRepository.save(entity);
        transactionHistorySearchRepository.index(entity);
        return transactionHistoryMapper.toDto(entity);
    }

    /**
     * Update a transactionHistory.
     *
     * @param transactionHistoryDTO the entity to save.
     * @return the persisted entity.
     */
    public TransactionHistoryDTO update(TransactionHistoryDTO transactionHistoryDTO) {
        LOG.debug("Request to update TransactionHistory: {}", transactionHistoryDTO);

        // Normalisation simple
        normalizePhoneNumbers(transactionHistoryDTO);

        TransactionHistory entity = transactionHistoryMapper.toEntity(transactionHistoryDTO);
        entity = transactionHistoryRepository.save(entity);
        transactionHistorySearchRepository.index(entity);
        return transactionHistoryMapper.toDto(entity);
    }

    /**
     * Partially update a transactionHistory.
     *
     * @param transactionHistoryDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<TransactionHistoryDTO> partialUpdate(TransactionHistoryDTO transactionHistoryDTO) {
        LOG.debug("Request to partially update TransactionHistory: {}", transactionHistoryDTO);

        return transactionHistoryRepository
            .findById(transactionHistoryDTO.getId())
            .map(existingTransactionHistory -> {
                // Normalisation seulement si les numéros sont fournis
                if (transactionHistoryDTO.getSenderPhone() != null) {
                    existingTransactionHistory.setSenderPhone(phoneNumberNormalizer.normalize(transactionHistoryDTO.getSenderPhone()));
                }
                if (transactionHistoryDTO.getReceiverPhone() != null) {
                    existingTransactionHistory.setReceiverPhone(phoneNumberNormalizer.normalize(transactionHistoryDTO.getReceiverPhone()));
                }

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

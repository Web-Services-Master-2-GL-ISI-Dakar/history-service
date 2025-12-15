package sn.ondmoney.history.web.rest;

import sn.ondmoney.history.domain.TransactionHistory;
import sn.ondmoney.history.domain.enumeration.TransactionStatus;
import sn.ondmoney.history.domain.enumeration.TransactionType;
import sn.ondmoney.history.repository.TransactionHistoryRepository;
import sn.ondmoney.history.service.TransactionHistoryService;
import sn.ondmoney.history.service.dto.TransactionHistoryDTO;
import sn.ondmoney.history.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link TransactionHistory}.
 */
@RestController
@RequestMapping("/api/transaction-histories")
public class TransactionHistoryResource {

    private static final Logger LOG = LoggerFactory.getLogger(TransactionHistoryResource.class);

    private static final String ENTITY_NAME = "transactionHistoryServiceTransactionHistory";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TransactionHistoryService transactionHistoryService;

    private final TransactionHistoryRepository transactionHistoryRepository;

    public TransactionHistoryResource(
        TransactionHistoryService transactionHistoryService,
        TransactionHistoryRepository transactionHistoryRepository
    ) {
        this.transactionHistoryService = transactionHistoryService;
        this.transactionHistoryRepository = transactionHistoryRepository;
    }

    /**
     * {@code POST  /transaction-histories} : Create a new transactionHistory.
     */
    @PostMapping("")
    public ResponseEntity<TransactionHistoryDTO> createTransactionHistory(@Valid @RequestBody TransactionHistoryDTO transactionHistoryDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save TransactionHistory : {}", transactionHistoryDTO);
        if (transactionHistoryDTO.getId() != null) {
            throw new BadRequestAlertException("A new transactionHistory cannot already have an ID", ENTITY_NAME, "idexists");
        }
        transactionHistoryDTO = transactionHistoryService.save(transactionHistoryDTO);
        return ResponseEntity.created(new URI("/api/transaction-histories/" + transactionHistoryDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, transactionHistoryDTO.getId()))
            .body(transactionHistoryDTO);
    }

    /**
     * {@code GET  /transaction-histories/search} : search transactions with multiple criteria using Elasticsearch.
     * Utilise l'implémentation existante du repository de recherche.
     * Exemples d'utilisation :
     * /api/transaction-histories/search?phoneNumber=00221771234567
     * /api/transaction-histories/search?type=DEPOSIT&status=SUCCESS
     * /api/transaction-histories/search?query=transfert+urgence
     * /api/transaction-histories/search?startDate=2024-01-01T00:00:00Z&endDate=2024-01-31T23:59:59Z
     * /api/transaction-histories/search?minAmount=1000&maxAmount=50000
     * /api/transaction-histories/search?phoneNumber=00221771234567&type=TRANSFER&status=SUCCESS&startDate=2024-01-01T00:00:00Z&endDate=2024-01-31T23:59:59Z
     */
    @GetMapping("/search")
    public ResponseEntity<List<TransactionHistoryDTO>> searchTransactions(
        @RequestParam(required = false) String senderPhone,
        @RequestParam(required = false) String receiverPhone,
        @RequestParam(required = false) TransactionType type,
        @RequestParam(required = false) TransactionStatus status,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startDate,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endDate,
        @RequestParam(required = false) BigDecimal minAmount,
        @RequestParam(required = false) BigDecimal maxAmount,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug(
            "REST request to search transactions - sender: {}, receiver: {}, type: {}, status: {}",
            senderPhone,
            receiverPhone,
            type,
            status
        );

        Page<TransactionHistoryDTO> page = transactionHistoryService.searchByCriteria(
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

        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code PUT  /transaction-histories/:id} : Updates an existing transactionHistory.
     */
    @PutMapping("/{id}")
    public ResponseEntity<TransactionHistoryDTO> updateTransactionHistory(
        @PathVariable(value = "id", required = false) final String id,
        @Valid @RequestBody TransactionHistoryDTO transactionHistoryDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update TransactionHistory : {}, {}", id, transactionHistoryDTO);
        if (transactionHistoryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, transactionHistoryDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!transactionHistoryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        transactionHistoryDTO = transactionHistoryService.update(transactionHistoryDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, transactionHistoryDTO.getId()))
            .body(transactionHistoryDTO);
    }

    /**
     * {@code PATCH  /transaction-histories/:id} : Partial updates given fields of an existing transactionHistory, field will ignore if it is null
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<TransactionHistoryDTO> partialUpdateTransactionHistory(
        @PathVariable(value = "id", required = false) final String id,
        @NotNull @RequestBody TransactionHistoryDTO transactionHistoryDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update TransactionHistory partially : {}, {}", id, transactionHistoryDTO);
        if (transactionHistoryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, transactionHistoryDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!transactionHistoryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TransactionHistoryDTO> result = transactionHistoryService.partialUpdate(transactionHistoryDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, transactionHistoryDTO.getId())
        );
    }

    /**
     * {@code GET  /transaction-histories} : get all the transactionHistories.
     */
    @GetMapping("")
    public ResponseEntity<List<TransactionHistoryDTO>> getAllTransactionHistories(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get a page of TransactionHistories");
        Page<TransactionHistoryDTO> page = transactionHistoryService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /transaction-histories/:id} : get the "id" transactionHistory.
     */
    @GetMapping("/{id}")
    public ResponseEntity<TransactionHistoryDTO> getTransactionHistory(@PathVariable("id") String id) {
        LOG.debug("REST request to get TransactionHistory : {}", id);
        Optional<TransactionHistoryDTO> transactionHistoryDTO = transactionHistoryService.findOne(id);
        return ResponseUtil.wrapOrNotFound(transactionHistoryDTO);
    }

    /**
     * {@code DELETE  /transaction-histories/:id} : delete the "id" transactionHistory.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransactionHistory(@PathVariable("id") String id) {
        LOG.debug("REST request to delete TransactionHistory : {}", id);
        transactionHistoryService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id)).build();
    }
}

package com.ondmoney.sn.history.web.graphql;

import com.ondmoney.sn.history.broker.TransactionTestDataGenerator;
import com.ondmoney.sn.history.domain.enumeration.TransactionStatus;
import com.ondmoney.sn.history.domain.enumeration.TransactionType;
import com.ondmoney.sn.history.service.TransactionHistoryService;
import com.ondmoney.sn.history.service.dto.TransactionHistoryDTO;
import com.ondmoney.sn.history.web.graphql.input.TransactionHistoryInput;
import com.ondmoney.sn.history.web.graphql.input.TransactionSearchInput;
import com.ondmoney.sn.history.web.graphql.response.TransactionPageResponse;
import com.ondmoney.sn.history.web.graphql.response.TransactionResponse;
import com.ondmoney.sn.history.web.graphql.response.TransactionTypesResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class TransactionHistoryGraphQLController {

    private static final Logger LOG = LoggerFactory.getLogger(TransactionHistoryGraphQLController.class);

    private final TransactionHistoryService transactionHistoryService;
    private final TransactionTestDataGenerator testDataGenerator;

    public TransactionHistoryGraphQLController(
        TransactionHistoryService transactionHistoryService,
        TransactionTestDataGenerator testDataGenerator
    ) {
        this.transactionHistoryService = transactionHistoryService;
        this.testDataGenerator = testDataGenerator;
    }

    @QueryMapping
    public TransactionPageResponse searchTransactions(@Argument TransactionSearchInput searchInput) {
        LOG.debug("GraphQL request to search transactions with input: {}", searchInput);

        Pageable pageable = PageRequest.of(
            searchInput.getPage() != null ? searchInput.getPage() : 0,
            searchInput.getSize() != null ? searchInput.getSize() : 20
        );

        Page<TransactionHistoryDTO> page = transactionHistoryService.searchByCriteria(
            searchInput.getSenderPhone(),
            searchInput.getReceiverPhone(),
            searchInput.getType(),
            searchInput.getStatus(),
            searchInput.getStartDate(),
            searchInput.getEndDate(),
            searchInput.getMinAmount(),
            searchInput.getMaxAmount(),
            pageable
        );

        return TransactionPageResponse.from(page);
    }

    @QueryMapping
    public TransactionHistoryDTO transactionHistory(@Argument String id) {
        LOG.debug("GraphQL request to get TransactionHistory : {}", id);

        Optional<TransactionHistoryDTO> result = transactionHistoryService.findOne(id);

        if (result.isPresent()) {
            TransactionHistoryDTO dto = result.get();
            LOG.debug("Found DTO - ID: {}, TransactionId: {}, Type: {}, Status: {}, Amount: {}, SenderPhone: {}, TransactionDate: {}",
                dto.getId(),
                dto.getTransactionId(),
                dto.getType(),
                dto.getStatus(),
                dto.getAmount(),
                dto.getSenderPhone(),
                dto.getTransactionDate()
            );
            return dto;
        } else {
            LOG.debug("No transaction found with id: {}", id);
            throw new RuntimeException("TransactionHistory not found with id: " + id);
        }
    }

    @QueryMapping
    public TransactionPageResponse allTransactionHistories(@Argument Integer page, @Argument Integer size) {
        LOG.debug("GraphQL request to get all TransactionHistories");

        Pageable pageable = PageRequest.of(
            page != null ? page : 0,
            size != null ? size : 20
        );

        Page<TransactionHistoryDTO> resultPage = transactionHistoryService.findAll(pageable);

        // Log the results
        if (!resultPage.getContent().isEmpty()) {
            TransactionHistoryDTO first = resultPage.getContent().get(0);
            LOG.debug("First transaction - ID: {}, TransactionId: {}, Type: {}",
                first.getId(), first.getTransactionId(), first.getType());
        } else {
            LOG.debug("No transactions found in database");
        }

        return TransactionPageResponse.from(resultPage);
    }

    @QueryMapping
    public TransactionTypesResponse transactionTypes() {
        LOG.debug("GraphQL request to get all transaction types");
        List<String> types = Arrays.stream(TransactionType.values())
            .map(Enum::toString)
            .collect(Collectors.toList());

        return new TransactionTypesResponse(types);
    }

    @MutationMapping
    public TransactionHistoryDTO createTransactionHistory(@Argument TransactionHistoryInput input) {
        LOG.debug("GraphQL request to create TransactionHistory : {}", input);

        TransactionHistoryDTO dto = mapInputToDTO(input);
        if (dto.getId() != null) {
            throw new RuntimeException("A new transactionHistory cannot already have an ID");
        }

        return transactionHistoryService.save(dto);
    }

    @MutationMapping
    public TransactionHistoryDTO updateTransactionHistory(@Argument String id, @Argument TransactionHistoryInput input) {
        LOG.debug("GraphQL request to update TransactionHistory : {}, {}", id, input);

        TransactionHistoryDTO dto = mapInputToDTO(input);
        dto.setId(id);

        return transactionHistoryService.update(dto);
    }

    @MutationMapping
    public TransactionHistoryDTO partialUpdateTransactionHistory(@Argument String id, @Argument TransactionHistoryInput input) {
        LOG.debug("GraphQL request to partial update TransactionHistory : {}, {}", id, input);

        TransactionHistoryDTO dto = mapInputToDTO(input);
        dto.setId(id);

        return transactionHistoryService.partialUpdate(dto)
            .orElseThrow(() -> new RuntimeException("TransactionHistory not found with id: " + id));
    }

    @MutationMapping
    public Boolean deleteTransactionHistory(@Argument String id) {
        LOG.debug("GraphQL request to delete TransactionHistory : {}", id);
        transactionHistoryService.delete(id);
        return true;
    }

    @MutationMapping
    public TransactionResponse generateTransaction(@Argument TransactionType type) {
        LOG.info("GraphQL request to generate test transaction of type: {}", type);
        testDataGenerator.generateTestTransaction(type);

        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Transaction " + type + " generated and sent to Kafka");
        response.put("type", type.toString());

        return new TransactionResponse("success", "Transaction " + type + " generated and sent to Kafka", type.toString());
    }

    @MutationMapping
    public TransactionResponse generateAllTransactions() {
        LOG.info("GraphQL request to generate all transaction types");

        List<String> results = Arrays.stream(TransactionType.values())
            .map(type -> {
                testDataGenerator.generateTestTransaction(type);
                return type.toString() + " - Generated";
            })
            .collect(Collectors.toList());

        return new TransactionResponse("success", "All transaction types generated and sent to Kafka", null);
    }

    private TransactionHistoryDTO mapInputToDTO(TransactionHistoryInput input) {
        TransactionHistoryDTO dto = new TransactionHistoryDTO();
        dto.setTransactionId(input.getTransactionId());
        dto.setExternalTransactionId(input.getExternalTransactionId());
        dto.setType(input.getType());
        dto.setStatus(input.getStatus());
        dto.setAmount(input.getAmount());
        dto.setCurrency(input.getCurrency());
        dto.setSenderPhone(input.getSenderPhone());
        dto.setReceiverPhone(input.getReceiverPhone());
        dto.setSenderName(input.getSenderName());
        dto.setReceiverName(input.getReceiverName());
        dto.setDescription(input.getDescription());
        dto.setFees(input.getFees());
        dto.setBalanceBefore(input.getBalanceBefore());
        dto.setBalanceAfter(input.getBalanceAfter());
        dto.setMerchantCode(input.getMerchantCode());
        dto.setBillReference(input.getBillReference());
        dto.setBankAccountNumber(input.getBankAccountNumber());
        dto.setTransactionDate(input.getTransactionDate());
        dto.setProcessingDate(input.getProcessingDate());
        dto.setCreatedBy(input.getCreatedBy());
        dto.setUserAgent(input.getUserAgent());
        dto.setIpAddress(input.getIpAddress());
        dto.setDeviceId(input.getDeviceId());
        dto.setMetadata(input.getMetadata());
        dto.setErrorMessage(input.getErrorMessage());
        dto.setCorrelationId(input.getCorrelationId());
        dto.setVersion(input.getVersion());
        dto.setHistorySaved(input.getHistorySaved());

        return dto;
    }
}

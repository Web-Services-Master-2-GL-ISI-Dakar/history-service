package sn.ondmoney.history.web.graphql;

import sn.ondmoney.history.broker.TransactionTestDataGenerator;
import sn.ondmoney.history.domain.enumeration.*;
import sn.ondmoney.history.service.TransactionHistoryService;
import sn.ondmoney.history.service.dto.TransactionHistoryDTO;
import sn.ondmoney.history.web.graphql.input.TransactionHistoryInput;
import sn.ondmoney.history.web.graphql.input.TransactionSearchInput;
import sn.ondmoney.history.web.graphql.response.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

        // Create pageable with sorting
        Sort sort = createSort(searchInput.getSortBy(), searchInput.getSortDirection());
        Pageable pageable = PageRequest.of(
            searchInput.getPage() != null ? searchInput.getPage() : 0,
            searchInput.getSize() != null ? searchInput.getSize() : 20,
            sort
        );

        Page<TransactionHistoryDTO> page = transactionHistoryService.searchByCriteria(
            searchInput.getSenderPhone(),
            searchInput.getReceiverPhone(),
            searchInput.getTypes(),
            searchInput.getStatuses(),
            searchInput.getStartDate(),
            searchInput.getEndDate(),
            searchInput.getMinAmount(),
            searchInput.getMaxAmount(),
            searchInput.getCurrency(),
            searchInput.getDirection(),
            searchInput.getMerchantCode(),
            searchInput.getBillReference(),
            searchInput.getBankAccountNumber(),
            searchInput.getDescriptionContains(),
            pageable
        );

        return TransactionPageResponse.from(page);
    }

    @QueryMapping
    public TransactionHistoryDTO transactionHistory(@Argument String id) {
        LOG.debug("GraphQL request to get TransactionHistory : {}", id);

        TransactionHistoryDTO dto = transactionHistoryService.findOne(id)
            .orElseThrow(() -> new RuntimeException("TransactionHistory not found with id: " + id));

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
    }

    @QueryMapping
    public TransactionPageResponse allTransactionHistories(
        @Argument Integer page,
        @Argument Integer size,
        @Argument TransactionSortField sortBy,
        @Argument SortDirection sortDirection
    ) {
        LOG.debug("GraphQL request to get all TransactionHistories");

        // Create pageable with sorting
        Sort sort = createSort(sortBy, sortDirection);
        Pageable pageable = PageRequest.of(
            page != null ? page : 0,
            size != null ? size : 20,
            sort
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

    // NEW: Get user's complete transaction history
    @QueryMapping
    public TransactionPageResponse userTransactions(
        @Argument String phoneNumber,
        @Argument Integer page,
        @Argument Integer size,
        @Argument List<TransactionType> types,
        @Argument List<TransactionStatus> statuses,
        @Argument Instant startDate,
        @Argument Instant endDate,
        @Argument TransactionDirection direction,
        @Argument TransactionSortField sortBy,
        @Argument SortDirection sortDirection
    ) {
        LOG.debug("GraphQL request to get user transactions for: {}", phoneNumber);

        // Create pageable with sorting
        Sort sort = createSort(sortBy, sortDirection);
        Pageable pageable = PageRequest.of(
            page != null ? page : 0,
            size != null ? size : 20,
            sort
        );

        // Build search input based on direction
        String senderPhone = null;
        String receiverPhone = null;
        TransactionDirection effectiveDirection = direction != null ? direction : TransactionDirection.ALL;

        switch (effectiveDirection) {
            case SENT:
                senderPhone = phoneNumber;
                break;
            case RECEIVED:
                receiverPhone = phoneNumber;
                break;
            case ALL:
                // For ALL direction, pass phoneNumber as senderPhone
                // The service layer will use it for both senderPhone and receiverPhone in OR query
                senderPhone = phoneNumber;
                break;
        }

        Page<TransactionHistoryDTO> userpage = transactionHistoryService.searchByCriteria(
            senderPhone,
            receiverPhone,
            types,
            statuses,
            startDate,
            endDate,
            null, // minAmount
            null, // maxAmount
            null, // currency
            effectiveDirection,
            null, // merchantCode
            null, // billReference
            null, // bankAccountNumber
            null, // descriptionContains
            pageable
        );

        return TransactionPageResponse.from(userpage);
    }

    // NEW: Get user transaction statistics
    @QueryMapping
    public UserTransactionStats userTransactionStats(
        @Argument String phoneNumber,
        @Argument Instant startDate,
        @Argument Instant endDate,
        @Argument List<TransactionType> types,
        @Argument TransactionDirection direction
    ) {
        LOG.debug("GraphQL request to get user transaction stats for: {}", phoneNumber);

        return transactionHistoryService.getUserTransactionStats(
            phoneNumber,
            startDate,
            endDate,
            types,
            direction
        );
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
            .toList();

        return new TransactionResponse("success", "All transaction types generated and sent to Kafka", null);
    }

    private TransactionHistoryDTO mapInputToDTO(TransactionHistoryInput input) {
        return new TransactionHistoryDTO();
    }

    // Helper method to create Sort object
    private Sort createSort(TransactionSortField sortBy, SortDirection sortDirection) {
        if (sortBy == null) {
            sortBy = TransactionSortField.TRANSACTION_DATE;
        }
        if (sortDirection == null) {
            sortDirection = SortDirection.DESC;
        }

        Sort.Direction direction = sortDirection == SortDirection.ASC ?
            Sort.Direction.ASC : Sort.Direction.DESC;

        String sortField;
        switch (sortBy) {
            case AMOUNT:
                sortField = "amount";
                break;
            case STATUS:
                sortField = "status";
                break;
            case TYPE:
                sortField = "type";
                break;
            case CREATED_AT:
                sortField = "createdDate";
                break;
            case TRANSACTION_DATE:
            default:
                sortField = "transactionDate";
                break;
        }

        return Sort.by(direction, sortField);
    }
}

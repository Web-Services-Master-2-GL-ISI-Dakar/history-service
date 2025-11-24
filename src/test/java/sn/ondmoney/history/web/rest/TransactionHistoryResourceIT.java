package sn.ondmoney.history.web.rest;

import static sn.ondmoney.history.domain.TransactionHistoryAsserts.*;
import static sn.ondmoney.history.web.rest.TestUtil.createUpdateProxyForBean;
import static sn.ondmoney.history.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import sn.ondmoney.history.IntegrationTest;
import sn.ondmoney.history.domain.TransactionHistory;
import sn.ondmoney.history.domain.enumeration.TransactionStatus;
import sn.ondmoney.history.domain.enumeration.TransactionType;
import sn.ondmoney.history.repository.TransactionHistoryRepository;
import sn.ondmoney.history.repository.search.TransactionHistorySearchRepository;
import sn.ondmoney.history.service.dto.TransactionHistoryDTO;
import sn.ondmoney.history.service.mapper.TransactionHistoryMapper;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.assertj.core.util.IterableUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.util.Streamable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Integration tests for the {@link TransactionHistoryResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TransactionHistoryResourceIT {

    private static final String DEFAULT_TRANSACTION_ID = "AAAAAAAAAA";
    private static final String UPDATED_TRANSACTION_ID = "BBBBBBBBBB";

    private static final String DEFAULT_EXTERNAL_TRANSACTION_ID = "AAAAAAAAAA";
    private static final String UPDATED_EXTERNAL_TRANSACTION_ID = "BBBBBBBBBB";

    private static final TransactionType DEFAULT_TYPE = TransactionType.DEPOSIT;
    private static final TransactionType UPDATED_TYPE = TransactionType.WITHDRAWAL;

    private static final TransactionStatus DEFAULT_STATUS = TransactionStatus.PENDING;
    private static final TransactionStatus UPDATED_STATUS = TransactionStatus.SUCCESS;

    private static final BigDecimal DEFAULT_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_AMOUNT = new BigDecimal(2);

    private static final String DEFAULT_CURRENCY = "AAA";
    private static final String UPDATED_CURRENCY = "BBB";

    private static final String DEFAULT_SENDER_PHONE = "AAAAAAAAAA";
    private static final String UPDATED_SENDER_PHONE = "BBBBBBBBBB";

    private static final String DEFAULT_RECEIVER_PHONE = "AAAAAAAAAA";
    private static final String UPDATED_RECEIVER_PHONE = "BBBBBBBBBB";

    private static final String DEFAULT_SENDER_NAME = "AAAAAAAAAA";
    private static final String UPDATED_SENDER_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_RECEIVER_NAME = "AAAAAAAAAA";
    private static final String UPDATED_RECEIVER_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_FEES = new BigDecimal(1);
    private static final BigDecimal UPDATED_FEES = new BigDecimal(2);

    private static final BigDecimal DEFAULT_BALANCE_BEFORE = new BigDecimal(1);
    private static final BigDecimal UPDATED_BALANCE_BEFORE = new BigDecimal(2);

    private static final BigDecimal DEFAULT_BALANCE_AFTER = new BigDecimal(1);
    private static final BigDecimal UPDATED_BALANCE_AFTER = new BigDecimal(2);

    private static final String DEFAULT_MERCHANT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_MERCHANT_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_BILL_REFERENCE = "AAAAAAAAAA";
    private static final String UPDATED_BILL_REFERENCE = "BBBBBBBBBB";

    private static final String DEFAULT_BANK_ACCOUNT_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_BANK_ACCOUNT_NUMBER = "BBBBBBBBBB";

    private static final Instant DEFAULT_TRANSACTION_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_TRANSACTION_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_PROCESSING_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_PROCESSING_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_CREATED_BY = "AAAAAAAAAA";
    private static final String UPDATED_CREATED_BY = "BBBBBBBBBB";

    private static final String DEFAULT_USER_AGENT = "AAAAAAAAAA";
    private static final String UPDATED_USER_AGENT = "BBBBBBBBBB";

    private static final String DEFAULT_IP_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_IP_ADDRESS = "BBBBBBBBBB";

    private static final String DEFAULT_DEVICE_ID = "AAAAAAAAAA";
    private static final String UPDATED_DEVICE_ID = "BBBBBBBBBB";

    private static final String DEFAULT_METADATA = "AAAAAAAAAA";
    private static final String UPDATED_METADATA = "BBBBBBBBBB";

    private static final String DEFAULT_ERROR_MESSAGE = "AAAAAAAAAA";
    private static final String UPDATED_ERROR_MESSAGE = "BBBBBBBBBB";

    private static final String DEFAULT_CORRELATION_ID = "AAAAAAAAAA";
    private static final String UPDATED_CORRELATION_ID = "BBBBBBBBBB";

    private static final Integer DEFAULT_VERSION = 1;
    private static final Integer UPDATED_VERSION = 2;

    private static final Boolean DEFAULT_HISTORY_SAVED = false;
    private static final Boolean UPDATED_HISTORY_SAVED = true;

    private static final String ENTITY_API_URL = "/api/transaction-histories";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/transaction-histories/_search";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TransactionHistoryRepository transactionHistoryRepository;

    @Autowired
    private TransactionHistoryMapper transactionHistoryMapper;

    @Autowired
    private TransactionHistorySearchRepository transactionHistorySearchRepository;

    @Autowired
    private MockMvc restTransactionHistoryMockMvc;

    private TransactionHistory transactionHistory;

    private TransactionHistory insertedTransactionHistory;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TransactionHistory createEntity() {
        return new TransactionHistory()
            .transactionId(DEFAULT_TRANSACTION_ID)
            .externalTransactionId(DEFAULT_EXTERNAL_TRANSACTION_ID)
            .type(DEFAULT_TYPE)
            .status(DEFAULT_STATUS)
            .amount(DEFAULT_AMOUNT)
            .currency(DEFAULT_CURRENCY)
            .senderPhone(DEFAULT_SENDER_PHONE)
            .receiverPhone(DEFAULT_RECEIVER_PHONE)
            .senderName(DEFAULT_SENDER_NAME)
            .receiverName(DEFAULT_RECEIVER_NAME)
            .description(DEFAULT_DESCRIPTION)
            .fees(DEFAULT_FEES)
            .balanceBefore(DEFAULT_BALANCE_BEFORE)
            .balanceAfter(DEFAULT_BALANCE_AFTER)
            .merchantCode(DEFAULT_MERCHANT_CODE)
            .billReference(DEFAULT_BILL_REFERENCE)
            .bankAccountNumber(DEFAULT_BANK_ACCOUNT_NUMBER)
            .transactionDate(DEFAULT_TRANSACTION_DATE)
            .processingDate(DEFAULT_PROCESSING_DATE)
            .createdBy(DEFAULT_CREATED_BY)
            .userAgent(DEFAULT_USER_AGENT)
            .ipAddress(DEFAULT_IP_ADDRESS)
            .deviceId(DEFAULT_DEVICE_ID)
            .metadata(DEFAULT_METADATA)
            .errorMessage(DEFAULT_ERROR_MESSAGE)
            .correlationId(DEFAULT_CORRELATION_ID)
            .version(DEFAULT_VERSION)
            .historySaved(DEFAULT_HISTORY_SAVED);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TransactionHistory createUpdatedEntity() {
        return new TransactionHistory()
            .transactionId(UPDATED_TRANSACTION_ID)
            .externalTransactionId(UPDATED_EXTERNAL_TRANSACTION_ID)
            .type(UPDATED_TYPE)
            .status(UPDATED_STATUS)
            .amount(UPDATED_AMOUNT)
            .currency(UPDATED_CURRENCY)
            .senderPhone(UPDATED_SENDER_PHONE)
            .receiverPhone(UPDATED_RECEIVER_PHONE)
            .senderName(UPDATED_SENDER_NAME)
            .receiverName(UPDATED_RECEIVER_NAME)
            .description(UPDATED_DESCRIPTION)
            .fees(UPDATED_FEES)
            .balanceBefore(UPDATED_BALANCE_BEFORE)
            .balanceAfter(UPDATED_BALANCE_AFTER)
            .merchantCode(UPDATED_MERCHANT_CODE)
            .billReference(UPDATED_BILL_REFERENCE)
            .bankAccountNumber(UPDATED_BANK_ACCOUNT_NUMBER)
            .transactionDate(UPDATED_TRANSACTION_DATE)
            .processingDate(UPDATED_PROCESSING_DATE)
            .createdBy(UPDATED_CREATED_BY)
            .userAgent(UPDATED_USER_AGENT)
            .ipAddress(UPDATED_IP_ADDRESS)
            .deviceId(UPDATED_DEVICE_ID)
            .metadata(UPDATED_METADATA)
            .errorMessage(UPDATED_ERROR_MESSAGE)
            .correlationId(UPDATED_CORRELATION_ID)
            .version(UPDATED_VERSION)
            .historySaved(UPDATED_HISTORY_SAVED);
    }

    @BeforeEach
    void initTest() {
        transactionHistory = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedTransactionHistory != null) {
            transactionHistoryRepository.delete(insertedTransactionHistory);
            transactionHistorySearchRepository.delete(insertedTransactionHistory);
            insertedTransactionHistory = null;
        }
    }

    @Test
    void createTransactionHistory() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionHistorySearchRepository.findAll());
        // Create the TransactionHistory
        TransactionHistoryDTO transactionHistoryDTO = transactionHistoryMapper.toDto(transactionHistory);
        var returnedTransactionHistoryDTO = om.readValue(
            restTransactionHistoryMockMvc
                .perform(
                    post(ENTITY_API_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsBytes(transactionHistoryDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            TransactionHistoryDTO.class
        );

        // Validate the TransactionHistory in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedTransactionHistory = transactionHistoryMapper.toEntity(returnedTransactionHistoryDTO);
        assertTransactionHistoryUpdatableFieldsEquals(
            returnedTransactionHistory,
            getPersistedTransactionHistory(returnedTransactionHistory)
        );

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionHistorySearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedTransactionHistory = returnedTransactionHistory;
    }

    @Test
    void createTransactionHistoryWithExistingId() throws Exception {
        // Create the TransactionHistory with an existing ID
        transactionHistory.setId("existing_id");
        TransactionHistoryDTO transactionHistoryDTO = transactionHistoryMapper.toDto(transactionHistory);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionHistorySearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restTransactionHistoryMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(transactionHistoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TransactionHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionHistorySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkTransactionIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionHistorySearchRepository.findAll());
        // set the field null
        transactionHistory.setTransactionId(null);

        // Create the TransactionHistory, which fails.
        TransactionHistoryDTO transactionHistoryDTO = transactionHistoryMapper.toDto(transactionHistory);

        restTransactionHistoryMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(transactionHistoryDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionHistorySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionHistorySearchRepository.findAll());
        // set the field null
        transactionHistory.setType(null);

        // Create the TransactionHistory, which fails.
        TransactionHistoryDTO transactionHistoryDTO = transactionHistoryMapper.toDto(transactionHistory);

        restTransactionHistoryMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(transactionHistoryDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionHistorySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionHistorySearchRepository.findAll());
        // set the field null
        transactionHistory.setStatus(null);

        // Create the TransactionHistory, which fails.
        TransactionHistoryDTO transactionHistoryDTO = transactionHistoryMapper.toDto(transactionHistory);

        restTransactionHistoryMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(transactionHistoryDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionHistorySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkAmountIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionHistorySearchRepository.findAll());
        // set the field null
        transactionHistory.setAmount(null);

        // Create the TransactionHistory, which fails.
        TransactionHistoryDTO transactionHistoryDTO = transactionHistoryMapper.toDto(transactionHistory);

        restTransactionHistoryMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(transactionHistoryDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionHistorySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkCurrencyIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionHistorySearchRepository.findAll());
        // set the field null
        transactionHistory.setCurrency(null);

        // Create the TransactionHistory, which fails.
        TransactionHistoryDTO transactionHistoryDTO = transactionHistoryMapper.toDto(transactionHistory);

        restTransactionHistoryMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(transactionHistoryDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionHistorySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkSenderPhoneIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionHistorySearchRepository.findAll());
        // set the field null
        transactionHistory.setSenderPhone(null);

        // Create the TransactionHistory, which fails.
        TransactionHistoryDTO transactionHistoryDTO = transactionHistoryMapper.toDto(transactionHistory);

        restTransactionHistoryMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(transactionHistoryDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionHistorySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkTransactionDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionHistorySearchRepository.findAll());
        // set the field null
        transactionHistory.setTransactionDate(null);

        // Create the TransactionHistory, which fails.
        TransactionHistoryDTO transactionHistoryDTO = transactionHistoryMapper.toDto(transactionHistory);

        restTransactionHistoryMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(transactionHistoryDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionHistorySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkHistorySavedIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionHistorySearchRepository.findAll());
        // set the field null
        transactionHistory.setHistorySaved(null);

        // Create the TransactionHistory, which fails.
        TransactionHistoryDTO transactionHistoryDTO = transactionHistoryMapper.toDto(transactionHistory);

        restTransactionHistoryMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(transactionHistoryDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionHistorySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllTransactionHistories() throws Exception {
        // Initialize the database
        insertedTransactionHistory = transactionHistoryRepository.save(transactionHistory);

        // Get all the transactionHistoryList
        restTransactionHistoryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(transactionHistory.getId())))
            .andExpect(jsonPath("$.[*].transactionId").value(hasItem(DEFAULT_TRANSACTION_ID)))
            .andExpect(jsonPath("$.[*].externalTransactionId").value(hasItem(DEFAULT_EXTERNAL_TRANSACTION_ID)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(sameNumber(DEFAULT_AMOUNT))))
            .andExpect(jsonPath("$.[*].currency").value(hasItem(DEFAULT_CURRENCY)))
            .andExpect(jsonPath("$.[*].senderPhone").value(hasItem(DEFAULT_SENDER_PHONE)))
            .andExpect(jsonPath("$.[*].receiverPhone").value(hasItem(DEFAULT_RECEIVER_PHONE)))
            .andExpect(jsonPath("$.[*].senderName").value(hasItem(DEFAULT_SENDER_NAME)))
            .andExpect(jsonPath("$.[*].receiverName").value(hasItem(DEFAULT_RECEIVER_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].fees").value(hasItem(sameNumber(DEFAULT_FEES))))
            .andExpect(jsonPath("$.[*].balanceBefore").value(hasItem(sameNumber(DEFAULT_BALANCE_BEFORE))))
            .andExpect(jsonPath("$.[*].balanceAfter").value(hasItem(sameNumber(DEFAULT_BALANCE_AFTER))))
            .andExpect(jsonPath("$.[*].merchantCode").value(hasItem(DEFAULT_MERCHANT_CODE)))
            .andExpect(jsonPath("$.[*].billReference").value(hasItem(DEFAULT_BILL_REFERENCE)))
            .andExpect(jsonPath("$.[*].bankAccountNumber").value(hasItem(DEFAULT_BANK_ACCOUNT_NUMBER)))
            .andExpect(jsonPath("$.[*].transactionDate").value(hasItem(DEFAULT_TRANSACTION_DATE.toString())))
            .andExpect(jsonPath("$.[*].processingDate").value(hasItem(DEFAULT_PROCESSING_DATE.toString())))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)))
            .andExpect(jsonPath("$.[*].userAgent").value(hasItem(DEFAULT_USER_AGENT)))
            .andExpect(jsonPath("$.[*].ipAddress").value(hasItem(DEFAULT_IP_ADDRESS)))
            .andExpect(jsonPath("$.[*].deviceId").value(hasItem(DEFAULT_DEVICE_ID)))
            .andExpect(jsonPath("$.[*].metadata").value(hasItem(DEFAULT_METADATA)))
            .andExpect(jsonPath("$.[*].errorMessage").value(hasItem(DEFAULT_ERROR_MESSAGE)))
            .andExpect(jsonPath("$.[*].correlationId").value(hasItem(DEFAULT_CORRELATION_ID)))
            .andExpect(jsonPath("$.[*].version").value(hasItem(DEFAULT_VERSION)))
            .andExpect(jsonPath("$.[*].historySaved").value(hasItem(DEFAULT_HISTORY_SAVED)));
    }

    @Test
    void getTransactionHistory() throws Exception {
        // Initialize the database
        insertedTransactionHistory = transactionHistoryRepository.save(transactionHistory);

        // Get the transactionHistory
        restTransactionHistoryMockMvc
            .perform(get(ENTITY_API_URL_ID, transactionHistory.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(transactionHistory.getId()))
            .andExpect(jsonPath("$.transactionId").value(DEFAULT_TRANSACTION_ID))
            .andExpect(jsonPath("$.externalTransactionId").value(DEFAULT_EXTERNAL_TRANSACTION_ID))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.amount").value(sameNumber(DEFAULT_AMOUNT)))
            .andExpect(jsonPath("$.currency").value(DEFAULT_CURRENCY))
            .andExpect(jsonPath("$.senderPhone").value(DEFAULT_SENDER_PHONE))
            .andExpect(jsonPath("$.receiverPhone").value(DEFAULT_RECEIVER_PHONE))
            .andExpect(jsonPath("$.senderName").value(DEFAULT_SENDER_NAME))
            .andExpect(jsonPath("$.receiverName").value(DEFAULT_RECEIVER_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.fees").value(sameNumber(DEFAULT_FEES)))
            .andExpect(jsonPath("$.balanceBefore").value(sameNumber(DEFAULT_BALANCE_BEFORE)))
            .andExpect(jsonPath("$.balanceAfter").value(sameNumber(DEFAULT_BALANCE_AFTER)))
            .andExpect(jsonPath("$.merchantCode").value(DEFAULT_MERCHANT_CODE))
            .andExpect(jsonPath("$.billReference").value(DEFAULT_BILL_REFERENCE))
            .andExpect(jsonPath("$.bankAccountNumber").value(DEFAULT_BANK_ACCOUNT_NUMBER))
            .andExpect(jsonPath("$.transactionDate").value(DEFAULT_TRANSACTION_DATE.toString()))
            .andExpect(jsonPath("$.processingDate").value(DEFAULT_PROCESSING_DATE.toString()))
            .andExpect(jsonPath("$.createdBy").value(DEFAULT_CREATED_BY))
            .andExpect(jsonPath("$.userAgent").value(DEFAULT_USER_AGENT))
            .andExpect(jsonPath("$.ipAddress").value(DEFAULT_IP_ADDRESS))
            .andExpect(jsonPath("$.deviceId").value(DEFAULT_DEVICE_ID))
            .andExpect(jsonPath("$.metadata").value(DEFAULT_METADATA))
            .andExpect(jsonPath("$.errorMessage").value(DEFAULT_ERROR_MESSAGE))
            .andExpect(jsonPath("$.correlationId").value(DEFAULT_CORRELATION_ID))
            .andExpect(jsonPath("$.version").value(DEFAULT_VERSION))
            .andExpect(jsonPath("$.historySaved").value(DEFAULT_HISTORY_SAVED));
    }

    @Test
    void getNonExistingTransactionHistory() throws Exception {
        // Get the transactionHistory
        restTransactionHistoryMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    void putExistingTransactionHistory() throws Exception {
        // Initialize the database
        insertedTransactionHistory = transactionHistoryRepository.save(transactionHistory);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        transactionHistorySearchRepository.save(transactionHistory);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionHistorySearchRepository.findAll());

        // Update the transactionHistory
        TransactionHistory updatedTransactionHistory = transactionHistoryRepository.findById(transactionHistory.getId()).orElseThrow();
        updatedTransactionHistory
            .transactionId(UPDATED_TRANSACTION_ID)
            .externalTransactionId(UPDATED_EXTERNAL_TRANSACTION_ID)
            .type(UPDATED_TYPE)
            .status(UPDATED_STATUS)
            .amount(UPDATED_AMOUNT)
            .currency(UPDATED_CURRENCY)
            .senderPhone(UPDATED_SENDER_PHONE)
            .receiverPhone(UPDATED_RECEIVER_PHONE)
            .senderName(UPDATED_SENDER_NAME)
            .receiverName(UPDATED_RECEIVER_NAME)
            .description(UPDATED_DESCRIPTION)
            .fees(UPDATED_FEES)
            .balanceBefore(UPDATED_BALANCE_BEFORE)
            .balanceAfter(UPDATED_BALANCE_AFTER)
            .merchantCode(UPDATED_MERCHANT_CODE)
            .billReference(UPDATED_BILL_REFERENCE)
            .bankAccountNumber(UPDATED_BANK_ACCOUNT_NUMBER)
            .transactionDate(UPDATED_TRANSACTION_DATE)
            .processingDate(UPDATED_PROCESSING_DATE)
            .createdBy(UPDATED_CREATED_BY)
            .userAgent(UPDATED_USER_AGENT)
            .ipAddress(UPDATED_IP_ADDRESS)
            .deviceId(UPDATED_DEVICE_ID)
            .metadata(UPDATED_METADATA)
            .errorMessage(UPDATED_ERROR_MESSAGE)
            .correlationId(UPDATED_CORRELATION_ID)
            .version(UPDATED_VERSION)
            .historySaved(UPDATED_HISTORY_SAVED);
        TransactionHistoryDTO transactionHistoryDTO = transactionHistoryMapper.toDto(updatedTransactionHistory);

        restTransactionHistoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, transactionHistoryDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(transactionHistoryDTO))
            )
            .andExpect(status().isOk());

        // Validate the TransactionHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedTransactionHistoryToMatchAllProperties(updatedTransactionHistory);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionHistorySearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<TransactionHistory> transactionHistorySearchList = Streamable.of(
                    transactionHistorySearchRepository.findAll()
                ).toList();
                TransactionHistory testTransactionHistorySearch = transactionHistorySearchList.get(searchDatabaseSizeAfter - 1);

                assertTransactionHistoryAllPropertiesEquals(testTransactionHistorySearch, updatedTransactionHistory);
            });
    }

    @Test
    void putNonExistingTransactionHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionHistorySearchRepository.findAll());
        transactionHistory.setId(UUID.randomUUID().toString());

        // Create the TransactionHistory
        TransactionHistoryDTO transactionHistoryDTO = transactionHistoryMapper.toDto(transactionHistory);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTransactionHistoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, transactionHistoryDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(transactionHistoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TransactionHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionHistorySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchTransactionHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionHistorySearchRepository.findAll());
        transactionHistory.setId(UUID.randomUUID().toString());

        // Create the TransactionHistory
        TransactionHistoryDTO transactionHistoryDTO = transactionHistoryMapper.toDto(transactionHistory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransactionHistoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(transactionHistoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TransactionHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionHistorySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamTransactionHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionHistorySearchRepository.findAll());
        transactionHistory.setId(UUID.randomUUID().toString());

        // Create the TransactionHistory
        TransactionHistoryDTO transactionHistoryDTO = transactionHistoryMapper.toDto(transactionHistory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransactionHistoryMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(transactionHistoryDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the TransactionHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionHistorySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateTransactionHistoryWithPatch() throws Exception {
        // Initialize the database
        insertedTransactionHistory = transactionHistoryRepository.save(transactionHistory);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the transactionHistory using partial update
        TransactionHistory partialUpdatedTransactionHistory = new TransactionHistory();
        partialUpdatedTransactionHistory.setId(transactionHistory.getId());

        partialUpdatedTransactionHistory
            .transactionId(UPDATED_TRANSACTION_ID)
            .externalTransactionId(UPDATED_EXTERNAL_TRANSACTION_ID)
            .receiverName(UPDATED_RECEIVER_NAME)
            .description(UPDATED_DESCRIPTION)
            .fees(UPDATED_FEES)
            .balanceAfter(UPDATED_BALANCE_AFTER)
            .transactionDate(UPDATED_TRANSACTION_DATE)
            .createdBy(UPDATED_CREATED_BY)
            .ipAddress(UPDATED_IP_ADDRESS)
            .version(UPDATED_VERSION)
            .historySaved(UPDATED_HISTORY_SAVED);

        restTransactionHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTransactionHistory.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTransactionHistory))
            )
            .andExpect(status().isOk());

        // Validate the TransactionHistory in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTransactionHistoryUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedTransactionHistory, transactionHistory),
            getPersistedTransactionHistory(transactionHistory)
        );
    }

    @Test
    void fullUpdateTransactionHistoryWithPatch() throws Exception {
        // Initialize the database
        insertedTransactionHistory = transactionHistoryRepository.save(transactionHistory);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the transactionHistory using partial update
        TransactionHistory partialUpdatedTransactionHistory = new TransactionHistory();
        partialUpdatedTransactionHistory.setId(transactionHistory.getId());

        partialUpdatedTransactionHistory
            .transactionId(UPDATED_TRANSACTION_ID)
            .externalTransactionId(UPDATED_EXTERNAL_TRANSACTION_ID)
            .type(UPDATED_TYPE)
            .status(UPDATED_STATUS)
            .amount(UPDATED_AMOUNT)
            .currency(UPDATED_CURRENCY)
            .senderPhone(UPDATED_SENDER_PHONE)
            .receiverPhone(UPDATED_RECEIVER_PHONE)
            .senderName(UPDATED_SENDER_NAME)
            .receiverName(UPDATED_RECEIVER_NAME)
            .description(UPDATED_DESCRIPTION)
            .fees(UPDATED_FEES)
            .balanceBefore(UPDATED_BALANCE_BEFORE)
            .balanceAfter(UPDATED_BALANCE_AFTER)
            .merchantCode(UPDATED_MERCHANT_CODE)
            .billReference(UPDATED_BILL_REFERENCE)
            .bankAccountNumber(UPDATED_BANK_ACCOUNT_NUMBER)
            .transactionDate(UPDATED_TRANSACTION_DATE)
            .processingDate(UPDATED_PROCESSING_DATE)
            .createdBy(UPDATED_CREATED_BY)
            .userAgent(UPDATED_USER_AGENT)
            .ipAddress(UPDATED_IP_ADDRESS)
            .deviceId(UPDATED_DEVICE_ID)
            .metadata(UPDATED_METADATA)
            .errorMessage(UPDATED_ERROR_MESSAGE)
            .correlationId(UPDATED_CORRELATION_ID)
            .version(UPDATED_VERSION)
            .historySaved(UPDATED_HISTORY_SAVED);

        restTransactionHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTransactionHistory.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTransactionHistory))
            )
            .andExpect(status().isOk());

        // Validate the TransactionHistory in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTransactionHistoryUpdatableFieldsEquals(
            partialUpdatedTransactionHistory,
            getPersistedTransactionHistory(partialUpdatedTransactionHistory)
        );
    }

    @Test
    void patchNonExistingTransactionHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionHistorySearchRepository.findAll());
        transactionHistory.setId(UUID.randomUUID().toString());

        // Create the TransactionHistory
        TransactionHistoryDTO transactionHistoryDTO = transactionHistoryMapper.toDto(transactionHistory);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTransactionHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, transactionHistoryDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(transactionHistoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TransactionHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionHistorySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchTransactionHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionHistorySearchRepository.findAll());
        transactionHistory.setId(UUID.randomUUID().toString());

        // Create the TransactionHistory
        TransactionHistoryDTO transactionHistoryDTO = transactionHistoryMapper.toDto(transactionHistory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransactionHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(transactionHistoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TransactionHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionHistorySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamTransactionHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionHistorySearchRepository.findAll());
        transactionHistory.setId(UUID.randomUUID().toString());

        // Create the TransactionHistory
        TransactionHistoryDTO transactionHistoryDTO = transactionHistoryMapper.toDto(transactionHistory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransactionHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(transactionHistoryDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the TransactionHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionHistorySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteTransactionHistory() throws Exception {
        // Initialize the database
        insertedTransactionHistory = transactionHistoryRepository.save(transactionHistory);
        transactionHistoryRepository.save(transactionHistory);
        transactionHistorySearchRepository.save(transactionHistory);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionHistorySearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the transactionHistory
        restTransactionHistoryMockMvc
            .perform(delete(ENTITY_API_URL_ID, transactionHistory.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionHistorySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchTransactionHistory() throws Exception {
        // Initialize the database
        insertedTransactionHistory = transactionHistoryRepository.save(transactionHistory);
        transactionHistorySearchRepository.save(transactionHistory);

        // Search the transactionHistory
        restTransactionHistoryMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + transactionHistory.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(transactionHistory.getId())))
            .andExpect(jsonPath("$.[*].transactionId").value(hasItem(DEFAULT_TRANSACTION_ID)))
            .andExpect(jsonPath("$.[*].externalTransactionId").value(hasItem(DEFAULT_EXTERNAL_TRANSACTION_ID)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(sameNumber(DEFAULT_AMOUNT))))
            .andExpect(jsonPath("$.[*].currency").value(hasItem(DEFAULT_CURRENCY)))
            .andExpect(jsonPath("$.[*].senderPhone").value(hasItem(DEFAULT_SENDER_PHONE)))
            .andExpect(jsonPath("$.[*].receiverPhone").value(hasItem(DEFAULT_RECEIVER_PHONE)))
            .andExpect(jsonPath("$.[*].senderName").value(hasItem(DEFAULT_SENDER_NAME)))
            .andExpect(jsonPath("$.[*].receiverName").value(hasItem(DEFAULT_RECEIVER_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].fees").value(hasItem(sameNumber(DEFAULT_FEES))))
            .andExpect(jsonPath("$.[*].balanceBefore").value(hasItem(sameNumber(DEFAULT_BALANCE_BEFORE))))
            .andExpect(jsonPath("$.[*].balanceAfter").value(hasItem(sameNumber(DEFAULT_BALANCE_AFTER))))
            .andExpect(jsonPath("$.[*].merchantCode").value(hasItem(DEFAULT_MERCHANT_CODE)))
            .andExpect(jsonPath("$.[*].billReference").value(hasItem(DEFAULT_BILL_REFERENCE)))
            .andExpect(jsonPath("$.[*].bankAccountNumber").value(hasItem(DEFAULT_BANK_ACCOUNT_NUMBER)))
            .andExpect(jsonPath("$.[*].transactionDate").value(hasItem(DEFAULT_TRANSACTION_DATE.toString())))
            .andExpect(jsonPath("$.[*].processingDate").value(hasItem(DEFAULT_PROCESSING_DATE.toString())))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)))
            .andExpect(jsonPath("$.[*].userAgent").value(hasItem(DEFAULT_USER_AGENT)))
            .andExpect(jsonPath("$.[*].ipAddress").value(hasItem(DEFAULT_IP_ADDRESS)))
            .andExpect(jsonPath("$.[*].deviceId").value(hasItem(DEFAULT_DEVICE_ID)))
            .andExpect(jsonPath("$.[*].metadata").value(hasItem(DEFAULT_METADATA.toString())))
            .andExpect(jsonPath("$.[*].errorMessage").value(hasItem(DEFAULT_ERROR_MESSAGE)))
            .andExpect(jsonPath("$.[*].correlationId").value(hasItem(DEFAULT_CORRELATION_ID)))
            .andExpect(jsonPath("$.[*].version").value(hasItem(DEFAULT_VERSION)))
            .andExpect(jsonPath("$.[*].historySaved").value(hasItem(DEFAULT_HISTORY_SAVED)));
    }

    protected long getRepositoryCount() {
        return transactionHistoryRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected TransactionHistory getPersistedTransactionHistory(TransactionHistory transactionHistory) {
        return transactionHistoryRepository.findById(transactionHistory.getId()).orElseThrow();
    }

    protected void assertPersistedTransactionHistoryToMatchAllProperties(TransactionHistory expectedTransactionHistory) {
        assertTransactionHistoryAllPropertiesEquals(expectedTransactionHistory, getPersistedTransactionHistory(expectedTransactionHistory));
    }

    protected void assertPersistedTransactionHistoryToMatchUpdatableProperties(TransactionHistory expectedTransactionHistory) {
        assertTransactionHistoryAllUpdatablePropertiesEquals(
            expectedTransactionHistory,
            getPersistedTransactionHistory(expectedTransactionHistory)
        );
    }
}

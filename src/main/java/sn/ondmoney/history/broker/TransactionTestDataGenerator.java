package sn.ondmoney.history.broker;

import com.fasterxml.jackson.databind.ObjectMapper;
import sn.ondmoney.history.domain.TransactionHistory;
import sn.ondmoney.history.domain.enumeration.TransactionStatus;
import sn.ondmoney.history.domain.enumeration.TransactionType;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Generates test transactions and publishes them as raw events to Kafka.
 */
@Component
public class TransactionTestDataGenerator {

    private static final Logger LOG = LoggerFactory.getLogger(TransactionTestDataGenerator.class);

    private final TransactionTxnEventProducer txnProducer;
    private final ObjectMapper objectMapper;
    private final Random random = new Random();

    private final List<String> senderPhones = Arrays.asList("+221771234567", "+221781234568", "+221791234569", "+221761234560");
    private final List<String> receiverPhones = Arrays.asList("+221771234500", "+221781234501", "+221791234502", "+221761234503");
    private final List<String> senderNames = Arrays.asList("Moussa Diop", "Aminata Fall", "Ibrahima Sow", "Fatou Bâ");
    private final List<String> receiverNames = Arrays.asList("Jean Dupont", "Marie Curie", "Paul Smith", "Anna Johnson");

    public TransactionTestDataGenerator(TransactionTxnEventProducer txnProducer,
                                        ObjectMapper objectMapper) {
        this.txnProducer = txnProducer;
        this.objectMapper = objectMapper;
    }

    /**
     * Generate a test transaction manually for a specific type
     */
    public void generateTestTransaction(TransactionType type) {
        try {
            TransactionHistory tx = createTransaction(type);
            txnProducer.publish(tx); // send to "transaction-events"
            LOG.info(">>> Raw transaction published: {} - {}", type, tx.getTransactionId());
        } catch (Exception e) {
            LOG.error("Error generating raw transaction", e);
        }
    }

    /**
     * Scheduled: generate one transaction of each type every 1 minute
     */
    @Scheduled(fixedRate = 60000)
    public void generateRandomTransactions() {
        for (TransactionType type : TransactionType.values()) {
            generateTestTransaction(type);
        }
    }

    /** Create a random transaction */
    private TransactionHistory createTransaction(TransactionType type) {
        TransactionHistory tx = new TransactionHistory();
        tx.setTransactionId(UUID.randomUUID().toString());
        tx.setExternalTransactionId("EXT-" + UUID.randomUUID().toString().substring(0, 8));
        tx.setType(type);
        tx.setStatus(getRandomStatus());
        tx.setAmount(generateRandomAmount(type));
        tx.setCurrency("XOF");
        tx.setSenderPhone(getRandomSenderPhone());
        tx.setReceiverPhone(getRandomReceiverPhone());
        tx.setSenderName(getRandomSenderName());
        tx.setReceiverName(getRandomReceiverName());
        tx.setDescription(generateDescription(type));
        tx.setFees(calculateFees(type));
        tx.setBalanceBefore(new BigDecimal("150000.00"));
        tx.setBalanceAfter(
            tx.getBalanceBefore()
                .subtract(tx.getAmount())
                .subtract(tx.getFees() != null ? tx.getFees() : BigDecimal.ZERO)
        );
        tx.setTransactionDate(Instant.now().minusSeconds(random.nextInt(86400))); // last 24h
        tx.setProcessingDate(Instant.now());
        tx.setCreatedBy("test-user");
        tx.setUserAgent("Mozilla/5.0 (Test Client)");
        tx.setIpAddress("192.168.1." + random.nextInt(255));
        tx.setDeviceId("device-" + random.nextInt(1000));
        tx.setMetadata("{\"test\": true, \"generated\": true}");
        tx.setCorrelationId(UUID.randomUUID().toString());
        tx.setVersion(1);
        tx.setHistorySaved(false);

        // Type-specific fields
        switch (type) {
            case MERCHANT_PAYMENT -> tx.setMerchantCode("MCH" + random.nextInt(1000));
            case BILL_PAYMENT -> tx.setBillReference("BILL" + random.nextInt(10000));
            case BANK_TRANSFER -> tx.setBankAccountNumber("SN" + random.nextInt(1000000000));
        }

        return tx;
    }

    private BigDecimal generateRandomAmount(TransactionType type) {
        return switch (type) {
            case DEPOSIT -> BigDecimal.valueOf(random.nextInt(500_000) + 1_000);
            case WITHDRAWAL -> BigDecimal.valueOf(random.nextInt(200_000) + 5_000);
            case TRANSFER -> BigDecimal.valueOf(random.nextInt(100_000) + 1_000);
            case BILL_PAYMENT -> BigDecimal.valueOf(random.nextInt(50_000) + 5_000);
            case AIRTIME -> BigDecimal.valueOf(random.nextInt(10_000) + 500);
            case MERCHANT_PAYMENT -> BigDecimal.valueOf(random.nextInt(50_000) + 1_000);
            case BANK_TRANSFER -> BigDecimal.valueOf(random.nextInt(300_000) + 5_000);
            case TOP_UP_CARD -> BigDecimal.valueOf(random.nextInt(50_000) + 1_000);
        };
    }

    private TransactionStatus getRandomStatus() {
        TransactionStatus[] statuses = TransactionStatus.values();
        return statuses[random.nextInt(statuses.length)];
    }

    private BigDecimal calculateFees(TransactionType type) {
        return switch (type) {
            case TRANSFER, BANK_TRANSFER -> BigDecimal.valueOf(500);
            case BILL_PAYMENT -> BigDecimal.valueOf(100);
            case WITHDRAWAL -> BigDecimal.valueOf(200);
            default -> BigDecimal.ZERO;
        };
    }

    private String generateDescription(TransactionType type) {
        return switch (type) {
            case DEPOSIT -> "Dépôt d'argent sur le compte";
            case WITHDRAWAL -> "Retrait d'espèces";
            case TRANSFER -> "Transfert d'argent à " + getRandomReceiverName();
            case BILL_PAYMENT -> "Paiement de facture SENELEc";
            case AIRTIME -> "Achat de crédit téléphonique";
            case MERCHANT_PAYMENT -> "Paiement marchand " + getRandomReceiverName();
            case BANK_TRANSFER -> "Transfert bancaire vers " + getRandomReceiverName();
            case TOP_UP_CARD -> "Recharge de carte électronique";
        };
    }

    private String getRandomSenderPhone() {
        return senderPhones.get(random.nextInt(senderPhones.size()));
    }

    private String getRandomReceiverPhone() {
        return receiverPhones.get(random.nextInt(receiverPhones.size()));
    }

    private String getRandomSenderName() {
        return senderNames.get(random.nextInt(senderNames.size()));
    }

    private String getRandomReceiverName() {
        return receiverNames.get(random.nextInt(receiverNames.size()));
    }
}

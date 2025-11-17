package com.ondmoney.sn.history.broker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ondmoney.sn.history.domain.TransactionHistory;
import com.ondmoney.sn.history.domain.enumeration.TransactionStatus;
import com.ondmoney.sn.history.domain.enumeration.TransactionType;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TransactionTestDataGenerator {

    private static final Logger LOG = LoggerFactory.getLogger(TransactionTestDataGenerator.class);

    private final StreamBridge streamBridge;
    private final ObjectMapper objectMapper;
    private final Random random = new Random();

    private final List<String> senderPhones = Arrays.asList("+221771234567", "+221781234568", "+221791234569", "+221761234560");

    private final List<String> receiverPhones = Arrays.asList("+221771234500", "+221781234501", "+221791234502", "+221761234503");

    private final List<String> senderNames = Arrays.asList("Moussa Diop", "Aminata Fall", "Ibrahima Sow", "Fatou Bâ");

    private final List<String> receiverNames = Arrays.asList("Jean Dupont", "Marie Curie", "Paul Smith", "Anna Johnson");

    public TransactionTestDataGenerator(StreamBridge streamBridge, ObjectMapper objectMapper) {
        this.streamBridge = streamBridge;
        this.objectMapper = objectMapper;
    }

    // Méthode pour générer un événement de test manuellement
    public void generateTestTransaction(TransactionType type) {
        try {
            TransactionHistory transaction = createTransaction(type);
            String transactionJson = objectMapper.writeValueAsString(transaction);

            boolean sent = streamBridge.send("transactionConsumer-in-0", transactionJson);

            if (sent) {
                LOG.info("Test transaction sent successfully: {} - {}", type, transaction.getTransactionId());
            } else {
                LOG.error("Failed to send test transaction: {}", type);
            }
        } catch (Exception e) {
            LOG.error("Error generating test transaction", e);
        }
    }

    // Génération automatique toutes les 30 secondes (optionnel)
    @Scheduled(fixedRate = 30000)
    public void generateRandomTransaction() {
        TransactionType[] types = TransactionType.values();
        TransactionType randomType = types[random.nextInt(types.length)];
        generateTestTransaction(randomType);
    }

    private TransactionHistory createTransaction(TransactionType type) {
        TransactionHistory transaction = new TransactionHistory();

        transaction.setTransactionId(UUID.randomUUID().toString());
        transaction.setExternalTransactionId("EXT-" + UUID.randomUUID().toString().substring(0, 8));
        transaction.setType(type);
        transaction.setStatus(getRandomStatus());
        transaction.setAmount(generateRandomAmount(type));
        transaction.setCurrency("XOF");
        transaction.setSenderPhone(getRandomSenderPhone());
        transaction.setReceiverPhone(getRandomReceiverPhone());
        transaction.setSenderName(getRandomSenderName());
        transaction.setReceiverName(getRandomReceiverName());
        transaction.setDescription(generateDescription(type));
        transaction.setFees(calculateFees(type));
        transaction.setBalanceBefore(new BigDecimal("150000.00"));
        transaction.setBalanceAfter(
            transaction
                .getBalanceBefore()
                .subtract(transaction.getAmount())
                .subtract(transaction.getFees() != null ? transaction.getFees() : BigDecimal.ZERO)
        );
        transaction.setTransactionDate(Instant.now().minusSeconds(random.nextInt(86400))); // Dans les dernières 24h
        transaction.setProcessingDate(Instant.now());
        transaction.setCreatedBy("test-user");
        transaction.setUserAgent("Mozilla/5.0 (Test Client)");
        transaction.setIpAddress("192.168.1." + random.nextInt(255));
        transaction.setDeviceId("device-" + random.nextInt(1000));
        transaction.setMetadata("{\"test\": true, \"generated\": true}");
        transaction.setCorrelationId(UUID.randomUUID().toString());
        transaction.setVersion(1);
        transaction.setHistorySaved(false);

        // Champs spécifiques selon le type
        switch (type) {
            case MERCHANT_PAYMENT:
                transaction.setMerchantCode("MCH" + random.nextInt(1000));
                break;
            case BILL_PAYMENT:
                transaction.setBillReference("BILL" + random.nextInt(10000));
                break;
            case BANK_TRANSFER:
                transaction.setBankAccountNumber("SN" + random.nextInt(1000000000));
                break;
        }

        return transaction;
    }

    private BigDecimal generateRandomAmount(TransactionType type) {
        switch (type) {
            case DEPOSIT:
                return new BigDecimal(random.nextInt(500000) + 1000); // 1000-500000
            case WITHDRAWAL:
                return new BigDecimal(random.nextInt(200000) + 5000); // 5000-200000
            case TRANSFER:
                return new BigDecimal(random.nextInt(100000) + 1000); // 1000-100000
            case BILL_PAYMENT:
                return new BigDecimal(random.nextInt(50000) + 5000); // 5000-50000
            case AIRTIME:
                return new BigDecimal(random.nextInt(10000) + 500); // 500-10000
            case MERCHANT_PAYMENT:
                return new BigDecimal(random.nextInt(50000) + 1000); // 1000-50000
            case BANK_TRANSFER:
                return new BigDecimal(random.nextInt(300000) + 5000); // 5000-300000
            case TOP_UP_CARD:
                return new BigDecimal(random.nextInt(50000) + 1000); // 1000-50000
            default:
                return new BigDecimal(random.nextInt(100000) + 1000);
        }
    }

    private TransactionStatus getRandomStatus() {
        TransactionStatus[] statuses = TransactionStatus.values();
        return statuses[random.nextInt(statuses.length)];
    }

    private BigDecimal calculateFees(TransactionType type) {
        switch (type) {
            case TRANSFER:
            case BANK_TRANSFER:
                return new BigDecimal("500");
            case BILL_PAYMENT:
                return new BigDecimal("100");
            case WITHDRAWAL:
                return new BigDecimal("200");
            default:
                return BigDecimal.ZERO;
        }
    }

    private String generateDescription(TransactionType type) {
        switch (type) {
            case DEPOSIT:
                return "Dépôt d'argent sur le compte";
            case WITHDRAWAL:
                return "Retrait d'espèces";
            case TRANSFER:
                return "Transfert d'argent à " + getRandomReceiverName();
            case BILL_PAYMENT:
                return "Paiement de facture SENELEc";
            case AIRTIME:
                return "Achat de crédit téléphonique";
            case MERCHANT_PAYMENT:
                return "Paiement marchand " + getRandomReceiverName();
            case BANK_TRANSFER:
                return "Transfert bancaire vers " + getRandomReceiverName();
            case TOP_UP_CARD:
                return "Recharge de carte électronique";
            default:
                return "Transaction " + type;
        }
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

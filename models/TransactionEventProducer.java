package sn.ondmoney.history.service;

import sn.ondmoney.history.domain.enumeration.TransactionStatus;
import sn.ondmoney.history.domain.enumeration.TransactionType;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;

@Service
public class TransactionEventProducer {

    private final Logger log = LoggerFactory.getLogger(TransactionEventProducer.class);
    private final StreamBridge streamBridge;

    public TransactionEventProducer(StreamBridge streamBridge) {
        this.streamBridge = streamBridge;
    }

    /**
     * Envoie un événement de test avec TOUS les champs pour tester le DTO
     */
    public void sendCompleteTestEvent() {
        try {
            TransactionEvent event = new TransactionEvent();

            // Champs obligatoires
            event.setTransactionId("COMPLETE_TEST_" + UUID.randomUUID().toString().substring(0, 8));
            event.setType(TransactionType.TRANSFER);
            event.setStatus(TransactionStatus.SUCCESS);
            event.setAmount(new BigDecimal("25000.00"));
            event.setCurrency("XOF");
            event.setSenderPhone("+221771234567");
            event.setReceiverPhone("+221771234568");
            event.setTransactionDate(Instant.now());

            // Champs optionnels avec données réalistes
            event.setSenderName("Jean Test");
            event.setReceiverName("Marie Test");
            event.setDescription("Transfert complet de test avec tous les champs");
            event.setFees(new BigDecimal("100.00"));
            event.setBalanceBefore(new BigDecimal("100000.00"));
            event.setBalanceAfter(new BigDecimal("74900.00"));
            event.setMerchantCode("MARCHAND_TEST");
            event.setBillReference("FACTURE_001");
            event.setBankAccountNumber("SN123456789");
            event.setProcessingDate(Instant.now());
            event.setCreatedBy("test-user");
            event.setUserAgent("Mobile-Android/1.0");
            event.setIpAddress("196.200.12.123");
            event.setDeviceId("DEVICE_" + UUID.randomUUID().toString().substring(0, 8));
            event.setMetadata("{\"test\": true, \"channel\": \"MOBILE_APP\", \"location\": \"Dakar\"}");
            event.setErrorMessage(null); // Pas d'erreur
            event.setCorrelationId("CORR_" + UUID.randomUUID().toString().substring(0, 8));
            event.setVersion(1);

            boolean sent = streamBridge.send("transactionProducer-out-0", event);

            if (sent) {
                log.info("✅ Événement COMPLET envoyé: {} - Tous les champs remplis", event.getTransactionId());
            } else {
                log.error("❌ Échec de l'envoi de l'événement complet");
            }
        } catch (Exception e) {
            log.error("❌ Erreur lors de l'envoi de l'événement complet: {}", e.getMessage(), e);
        }
    }

    /**
     * Envoie un événement de test simple
     */
    public void sendTestTransactionEvent() {
        try {
            TransactionEvent event = new TransactionEvent();
            event.setTransactionId("TEST_" + UUID.randomUUID().toString().substring(0, 8));
            event.setType(sn.ondmoney.history.domain.enumeration.TransactionType.TRANSFER);
            event.setStatus(sn.ondmoney.history.domain.enumeration.TransactionStatus.SUCCESS);
            event.setAmount(new BigDecimal("25000.00"));
            event.setCurrency("XOF");
            event.setSenderPhone("+221771234567");
            event.setReceiverPhone("+221771234568");
            event.setDescription("Transfert de test via Kafka Stream");
            event.setFees(new BigDecimal("100.00"));
            event.setTransactionDate(Instant.now());
            event.setCorrelationId("TEST_CORR_" + UUID.randomUUID());

            // Envoyer via Spring Cloud Stream
            boolean sent = streamBridge.send("transactionProducer-out-0", event);

            if (sent) {
                log.info("✅ Événement de test envoyé via StreamBridge: {}", event.getTransactionId());
            } else {
                log.error("❌ Échec de l'envoi de l'événement via StreamBridge");
            }
        } catch (Exception e) {
            log.error("❌ Erreur lors de l'envoi de l'événement: {}", e.getMessage(), e);
        }
    }

    /**
     * Envoie plusieurs événements de test basiques
     */
    public void sendMultipleTestEvents() {
        log.info("Envoi de multiples événements de test via Spring Cloud Stream...");

        sendTransaction("DEPOSIT", "+221771234567", "+221771234567", new BigDecimal("50000.00"));
        sendTransaction("WITHDRAWAL", "+221771234568", "+221771234568", new BigDecimal("20000.00"));
        sendTransaction("TRANSFER", "+221771234567", "+221771234569", new BigDecimal("15000.00"));
        sendTransaction("BILL_PAYMENT", "+221771234570", "SENELEC", new BigDecimal("12500.00"));
        sendTransaction("AIRTIME", "+221771234571", "+221771234571", new BigDecimal("5000.00"));

        log.info("✅ Tous les événements de test ont été envoyés");
    }

    /**
     * Envoie un événement pour chaque type de transaction avec des données réalistes - VERSION CORRIGÉE
     */
    public void sendAllTransactionTypes() {
        log.info("🚀 Envoi de tous les types de transactions...");

        try {
            // 1. DÉPÔT
            sendDeposit("+221771234567", new BigDecimal("75000.00"), "Agence Dakar Plateau");

            Thread.sleep(100); // Petit délai

            // 2. RETRAIT
            sendWithdrawal("+221771234568", new BigDecimal("35000.00"), "Guichet Ouakam");

            Thread.sleep(100);

            // 3. TRANSFERT - CORRIGÉ
            sendTransfer("+221771234567", "+221771234569", new BigDecimal("25000.00"), "Aide familiale");

            Thread.sleep(100);

            // 4. PAIEMENT DE FACTURE
            sendBillPayment("+221771234570", "SENELEC", "INV-2024-001", new BigDecimal("18500.00"), "Facture électricité");

            Thread.sleep(100);

            // 5. ACHAT DE CRÉDIT
            sendAirtime("+221771234571", new BigDecimal("5000.00"), "Recharge téléphonique");

            Thread.sleep(100);

            // 6. PAIEMENT MARCHAND
            sendMerchantPayment("+221771234572", "CAFE_TOUT", new BigDecimal("7500.00"), "Achat au Café Touba");

            Thread.sleep(100);

            // 7. TRANSFERT BANCAIRE
            sendBankTransfer("+221771234573", "SN123456789", new BigDecimal("150000.00"), "Transfert vers compte bancaire");

            Thread.sleep(100);

            // 8. RECHARGE DE CARTE
            sendCardTopUp("+221771234574", "CARTE_789", new BigDecimal("20000.00"), "Recharge carte de transport");

            log.info("✅ Tous les types de transactions ont été envoyés !");
        } catch (Exception e) {
            log.error("❌ Erreur lors de l'envoi des transactions: {}", e.getMessage(), e);
            throw new RuntimeException("Erreur d'envoi des transactions de test", e);
        }
    }

    /**
     * DÉPÔT - Un client dépose de l'argent sur son compte
     */
    private void sendDeposit(String phone, BigDecimal amount, String location) {
        TransactionEvent event = createBaseEvent("DEPOSIT", phone, phone, amount);
        event.setDescription("Dépôt d'argent - " + location);

        // CORRECTION : Vérifier que amount n'est pas null avant de faire des opérations
        if (amount != null) {
            event.setBalanceBefore(amount.subtract(new BigDecimal("5000.00")));
            event.setBalanceAfter(amount);
        } else {
            event.setBalanceBefore(new BigDecimal("0.00"));
            event.setBalanceAfter(new BigDecimal("0.00"));
        }
        event.setFees(new BigDecimal("0.00"));

        sendEvent(event, "Dépôt");
    }

    /**
     * RETRAIT - Un client retire de l'argent de son compte
     */
    private void sendWithdrawal(String phone, BigDecimal amount, String location) {
        TransactionEvent event = createBaseEvent("WITHDRAWAL", phone, phone, amount);
        event.setDescription("Retrait d'argent - " + location);
        event.setBalanceBefore(safeAdd(amount, new BigDecimal("100000.00")));
        event.setBalanceAfter(safeSubtract(event.getBalanceBefore(), amount));
        event.setFees(new BigDecimal("500.00"));

        sendEvent(event, "Retrait");
    }

    /**
     * TRANSFERT - Transfert d'argent vers un autre numéro - VERSION CORRIGÉE
     */
    private void sendTransfer(String sender, String receiver, BigDecimal amount, String reason) {
        TransactionEvent event = createBaseEvent("TRANSFER", sender, receiver, amount);
        event.setDescription("Transfert d'argent - " + reason);
        event.setSenderName("Moussa Diallo");
        event.setReceiverName("Aminata Sow");
        event.setBalanceBefore(new BigDecimal("120000.00"));

        // CORRECTION : Vérifier que les BigDecimal ne sont pas null
        BigDecimal fees = event.getFees() != null ? event.getFees() : BigDecimal.ZERO;
        event.setBalanceAfter(event.getBalanceBefore().subtract(amount).subtract(fees));

        // S'assurer que les frais sont définis
        event.setFees(new BigDecimal("100.00")); // Frais de transfert

        sendEvent(event, "Transfert");
    }

    /**
     * PAIEMENT DE FACTURE - Paiement de services (SENELEC, SONEAU, etc.)
     */
    private void sendBillPayment(String phone, String provider, String billRef, BigDecimal amount, String description) {
        TransactionEvent event = createBaseEvent("BILL_PAYMENT", phone, null, amount);
        event.setBillReference(billRef);
        event.setDescription(description + " - " + provider);
        event.setBalanceBefore(new BigDecimal("80000.00"));
        event.setBalanceAfter(event.getBalanceBefore().subtract(amount));
        event.setFees(new BigDecimal("150.00")); // Frais de paiement facture

        sendEvent(event, "Paiement facture");
    }

    /**
     * ACHAT DE CRÉDIT - Recharge téléphonique
     */
    private void sendAirtime(String phone, BigDecimal amount, String description) {
        TransactionEvent event = createBaseEvent("AIRTIME", phone, phone, amount);
        event.setDescription(description);
        event.setBalanceBefore(new BigDecimal("45000.00"));
        event.setBalanceAfter(event.getBalanceBefore().subtract(amount));
        event.setFees(new BigDecimal("0.00")); // Pas de frais pour recharge

        sendEvent(event, "Achat crédit");
    }

    /**
     * PAIEMENT MARCHAND - Paiement chez un commerçant
     */
    private void sendMerchantPayment(String phone, String merchantCode, BigDecimal amount, String description) {
        TransactionEvent event = createBaseEvent("MERCHANT_PAYMENT", phone, null, amount);
        event.setMerchantCode(merchantCode);
        event.setDescription(description);
        event.setBalanceBefore(new BigDecimal("60000.00"));
        event.setBalanceAfter(event.getBalanceBefore().subtract(amount));
        event.setFees(new BigDecimal("50.00")); // Frais de transaction marchand

        sendEvent(event, "Paiement marchand");
    }

    /**
     * TRANSFERT BANCAIRE - Transfert vers un compte bancaire
     */
    private void sendBankTransfer(String phone, String bankAccount, BigDecimal amount, String description) {
        TransactionEvent event = createBaseEvent("BANK_TRANSFER", phone, null, amount);
        event.setBankAccountNumber(bankAccount);
        event.setDescription(description);
        event.setBalanceBefore(new BigDecimal("300000.00"));
        event.setBalanceAfter(event.getBalanceBefore().subtract(amount).subtract(event.getFees()));
        event.setFees(new BigDecimal("500.00")); // Frais de transfert bancaire

        sendEvent(event, "Transfert bancaire");
    }

    /**
     * RECHARGE DE CARTE - Recharge de carte prépayée (transport, etc.)
     */
    private void sendCardTopUp(String phone, String cardNumber, BigDecimal amount, String description) {
        TransactionEvent event = createBaseEvent("TOP_UP_CARD", phone, null, amount);
        event.setDescription(description);
        event.setBalanceBefore(new BigDecimal("55000.00"));
        event.setBalanceAfter(event.getBalanceBefore().subtract(amount));
        event.setFees(new BigDecimal("25.00")); // Frais de recharge carte

        sendEvent(event, "Recharge carte");
    }

    /**
     * Méthode utilitaire pour envoyer des transactions basiques
     */
    private void sendTransaction(String type, String sender, String receiver, BigDecimal amount) {
        try {
            TransactionEvent event = new TransactionEvent();
            event.setTransactionId(type + "_TEST_" + UUID.randomUUID().toString().substring(0, 6));
            event.setType(sn.ondmoney.history.domain.enumeration.TransactionType.valueOf(type));
            event.setStatus(sn.ondmoney.history.domain.enumeration.TransactionStatus.SUCCESS);
            event.setAmount(amount);
            event.setCurrency("XOF");
            event.setSenderPhone(sender);

            if (type.equals("TRANSFER")) {
                event.setReceiverPhone(receiver);
                event.setDescription("Transfert vers " + receiver);
            } else if (type.equals("BILL_PAYMENT")) {
                event.setBillReference(receiver);
                event.setDescription("Paiement facture " + receiver);
            } else {
                event.setReceiverPhone(sender);
                event.setDescription(type.toLowerCase() + " d'argent");
            }

            event.setFees(new BigDecimal("100.00"));
            event.setTransactionDate(Instant.now());
            event.setCorrelationId("TEST_" + UUID.randomUUID().toString().substring(0, 8));

            boolean sent = streamBridge.send("transactionProducer-out-0", event);

            if (sent) {
                log.info("✅ {} envoyé via StreamBridge: {}", type, event.getTransactionId());
            } else {
                log.error("❌ Échec de l'envoi de {}", type);
            }
        } catch (Exception e) {
            log.error("❌ Erreur lors de l'envoi de {}: {}", type, e.getMessage());
        }
    }

    /**
     * Crée un événement de base avec les champs communs - VERSION CORRIGÉE
     */
    private TransactionEvent createBaseEvent(String type, String sender, String receiver, BigDecimal amount) {
        TransactionEvent event = new TransactionEvent();
        event.setTransactionId(type + "_TEST_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        event.setType(sn.ondmoney.history.domain.enumeration.TransactionType.valueOf(type));
        event.setStatus(sn.ondmoney.history.domain.enumeration.TransactionStatus.SUCCESS);
        event.setAmount(amount);
        event.setCurrency("XOF");
        event.setSenderPhone(sender);
        event.setReceiverPhone(receiver);
        event.setTransactionDate(Instant.now());
        event.setProcessingDate(Instant.now());
        event.setCreatedBy("system-test");
        event.setUserAgent("Mobile-Android");
        event.setIpAddress("196.200.12." + (int) (Math.random() * 255));
        event.setDeviceId("DEV" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        event.setMetadata("{\"test\": true, \"channel\": \"MOBILE_APP\"}");
        event.setCorrelationId("CORR_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        event.setVersion(1);

        // CORRECTION : Initialiser les champs BigDecimal pour éviter les null
        event.setFees(BigDecimal.ZERO);
        event.setBalanceBefore(BigDecimal.ZERO);
        event.setBalanceAfter(BigDecimal.ZERO);

        return event;
    }

    /**
     * Envoie l'événement via StreamBridge
     */
    private void sendEvent(TransactionEvent event, String transactionType) {
        try {
            boolean sent = streamBridge.send("transactionProducer-out-0", event);
            if (sent) {
                log.info("✅ {} envoyé: {} - {} XOF", transactionType, event.getTransactionId(), event.getAmount());
            } else {
                log.error("❌ Échec de l'envoi du {}", transactionType);
            }
            // Petit délai entre les envois
            Thread.sleep(100);
        } catch (Exception e) {
            log.error("❌ Erreur lors de l'envoi du {}: {}", transactionType, e.getMessage());
        }
    }

    // Classe pour les événements de transaction
    public static class TransactionEvent {

        private String transactionId;
        private sn.ondmoney.history.domain.enumeration.TransactionType type;
        private sn.ondmoney.history.domain.enumeration.TransactionStatus status;
        private BigDecimal amount;
        private String currency;
        private String senderPhone;
        private String receiverPhone;
        private String senderName;
        private String receiverName;
        private String description;
        private BigDecimal fees;
        private BigDecimal balanceBefore;
        private BigDecimal balanceAfter;
        private String merchantCode;
        private String billReference;
        private String bankAccountNumber;
        private Instant transactionDate;
        private Instant processingDate;
        private String createdBy;
        private String userAgent;
        private String ipAddress;
        private String deviceId;
        private String metadata;
        private String errorMessage;
        private String correlationId;
        private Integer version;

        // Getters and setters pour tous les champs
        public String getTransactionId() {
            return transactionId;
        }

        public void setTransactionId(String transactionId) {
            this.transactionId = transactionId;
        }

        public sn.ondmoney.history.domain.enumeration.TransactionType getType() {
            return type;
        }

        public void setType(sn.ondmoney.history.domain.enumeration.TransactionType type) {
            this.type = type;
        }

        public sn.ondmoney.history.domain.enumeration.TransactionStatus getStatus() {
            return status;
        }

        public void setStatus(sn.ondmoney.history.domain.enumeration.TransactionStatus status) {
            this.status = status;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        public String getSenderPhone() {
            return senderPhone;
        }

        public void setSenderPhone(String senderPhone) {
            this.senderPhone = senderPhone;
        }

        public String getReceiverPhone() {
            return receiverPhone;
        }

        public void setReceiverPhone(String receiverPhone) {
            this.receiverPhone = receiverPhone;
        }

        public String getSenderName() {
            return senderName;
        }

        public void setSenderName(String senderName) {
            this.senderName = senderName;
        }

        public String getReceiverName() {
            return receiverName;
        }

        public void setReceiverName(String receiverName) {
            this.receiverName = receiverName;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public BigDecimal getFees() {
            return fees;
        }

        public void setFees(BigDecimal fees) {
            this.fees = fees;
        }

        public BigDecimal getBalanceBefore() {
            return balanceBefore;
        }

        public void setBalanceBefore(BigDecimal balanceBefore) {
            this.balanceBefore = balanceBefore;
        }

        public BigDecimal getBalanceAfter() {
            return balanceAfter;
        }

        public void setBalanceAfter(BigDecimal balanceAfter) {
            this.balanceAfter = balanceAfter;
        }

        public String getMerchantCode() {
            return merchantCode;
        }

        public void setMerchantCode(String merchantCode) {
            this.merchantCode = merchantCode;
        }

        public String getBillReference() {
            return billReference;
        }

        public void setBillReference(String billReference) {
            this.billReference = billReference;
        }

        public String getBankAccountNumber() {
            return bankAccountNumber;
        }

        public void setBankAccountNumber(String bankAccountNumber) {
            this.bankAccountNumber = bankAccountNumber;
        }

        public Instant getTransactionDate() {
            return transactionDate;
        }

        public void setTransactionDate(Instant transactionDate) {
            this.transactionDate = transactionDate;
        }

        public Instant getProcessingDate() {
            return processingDate;
        }

        public void setProcessingDate(Instant processingDate) {
            this.processingDate = processingDate;
        }

        public String getCreatedBy() {
            return createdBy;
        }

        public void setCreatedBy(String createdBy) {
            this.createdBy = createdBy;
        }

        public String getUserAgent() {
            return userAgent;
        }

        public void setUserAgent(String userAgent) {
            this.userAgent = userAgent;
        }

        public String getIpAddress() {
            return ipAddress;
        }

        public void setIpAddress(String ipAddress) {
            this.ipAddress = ipAddress;
        }

        public String getDeviceId() {
            return deviceId;
        }

        public void setDeviceId(String deviceId) {
            this.deviceId = deviceId;
        }

        public String getMetadata() {
            return metadata;
        }

        public void setMetadata(String metadata) {
            this.metadata = metadata;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        public String getCorrelationId() {
            return correlationId;
        }

        public void setCorrelationId(String correlationId) {
            this.correlationId = correlationId;
        }

        public Integer getVersion() {
            return version;
        }

        public void setVersion(Integer version) {
            this.version = version;
        }
    }

    /**
     * Méthode utilitaire pour les opérations BigDecimal sécurisées
     */
    private BigDecimal safeSubtract(BigDecimal value1, BigDecimal value2) {
        if (value1 == null) value1 = BigDecimal.ZERO;
        if (value2 == null) value2 = BigDecimal.ZERO;
        return value1.subtract(value2);
    }

    /**
     * Méthode utilitaire pour les additions BigDecimal sécurisées
     */
    private BigDecimal safeAdd(BigDecimal value1, BigDecimal value2) {
        if (value1 == null) value1 = BigDecimal.ZERO;
        if (value2 == null) value2 = BigDecimal.ZERO;
        return value1.add(value2);
    }
}

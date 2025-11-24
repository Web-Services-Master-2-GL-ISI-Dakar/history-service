package com.ondmoney.sn.history.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class TransactionHistoryTestSamples {

    private static final Random random = new Random();
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static TransactionHistory getTransactionHistorySample1() {
        return new TransactionHistory()
            .id("id1")
            .transactionId("transactionId1")
            .externalTransactionId("externalTransactionId1")
            .currency("currency1")
            .senderPhone("senderPhone1")
            .receiverPhone("receiverPhone1")
            .senderName("senderName1")
            .receiverName("receiverName1")
            .merchantCode("merchantCode1")
            .billReference("billReference1")
            .bankAccountNumber("bankAccountNumber1")
            .createdBy("createdBy1")
            .userAgent("userAgent1")
            .ipAddress("ipAddress1")
            .deviceId("deviceId1")
            .errorMessage("errorMessage1")
            .correlationId("correlationId1")
            .version(1);
    }

    public static TransactionHistory getTransactionHistorySample2() {
        return new TransactionHistory()
            .id("id2")
            .transactionId("transactionId2")
            .externalTransactionId("externalTransactionId2")
            .currency("currency2")
            .senderPhone("senderPhone2")
            .receiverPhone("receiverPhone2")
            .senderName("senderName2")
            .receiverName("receiverName2")
            .merchantCode("merchantCode2")
            .billReference("billReference2")
            .bankAccountNumber("bankAccountNumber2")
            .createdBy("createdBy2")
            .userAgent("userAgent2")
            .ipAddress("ipAddress2")
            .deviceId("deviceId2")
            .errorMessage("errorMessage2")
            .correlationId("correlationId2")
            .version(2);
    }

    public static TransactionHistory getTransactionHistoryRandomSampleGenerator() {
        return new TransactionHistory()
            .id(UUID.randomUUID().toString())
            .transactionId(UUID.randomUUID().toString())
            .externalTransactionId(UUID.randomUUID().toString())
            .currency(UUID.randomUUID().toString())
            .senderPhone(UUID.randomUUID().toString())
            .receiverPhone(UUID.randomUUID().toString())
            .senderName(UUID.randomUUID().toString())
            .receiverName(UUID.randomUUID().toString())
            .merchantCode(UUID.randomUUID().toString())
            .billReference(UUID.randomUUID().toString())
            .bankAccountNumber(UUID.randomUUID().toString())
            .createdBy(UUID.randomUUID().toString())
            .userAgent(UUID.randomUUID().toString())
            .ipAddress(UUID.randomUUID().toString())
            .deviceId(UUID.randomUUID().toString())
            .errorMessage(UUID.randomUUID().toString())
            .correlationId(UUID.randomUUID().toString())
            .version(intCount.incrementAndGet());
    }
}

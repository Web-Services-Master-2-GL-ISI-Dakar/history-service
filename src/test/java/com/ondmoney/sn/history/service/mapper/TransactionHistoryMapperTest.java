package com.ondmoney.sn.history.service.mapper;

import static com.ondmoney.sn.history.domain.TransactionHistoryAsserts.*;
import static com.ondmoney.sn.history.domain.TransactionHistoryTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TransactionHistoryMapperTest {

    private TransactionHistoryMapper transactionHistoryMapper;

    @BeforeEach
    void setUp() {
        transactionHistoryMapper = new TransactionHistoryMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getTransactionHistorySample1();
        var actual = transactionHistoryMapper.toEntity(transactionHistoryMapper.toDto(expected));
        assertTransactionHistoryAllPropertiesEquals(expected, actual);
    }
}

package sn.ondmoney.history.service.mapper;

import static sn.ondmoney.history.domain.TransactionHistoryAsserts.*;
import static sn.ondmoney.history.domain.TransactionHistoryTestSamples.*;

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

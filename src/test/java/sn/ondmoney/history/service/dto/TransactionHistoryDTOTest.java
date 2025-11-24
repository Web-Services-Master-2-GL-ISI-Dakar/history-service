package sn.ondmoney.history.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import sn.ondmoney.history.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TransactionHistoryDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(TransactionHistoryDTO.class);
        TransactionHistoryDTO transactionHistoryDTO1 = new TransactionHistoryDTO();
        transactionHistoryDTO1.setId("id1");
        TransactionHistoryDTO transactionHistoryDTO2 = new TransactionHistoryDTO();
        assertThat(transactionHistoryDTO1).isNotEqualTo(transactionHistoryDTO2);
        transactionHistoryDTO2.setId(transactionHistoryDTO1.getId());
        assertThat(transactionHistoryDTO1).isEqualTo(transactionHistoryDTO2);
        transactionHistoryDTO2.setId("id2");
        assertThat(transactionHistoryDTO1).isNotEqualTo(transactionHistoryDTO2);
        transactionHistoryDTO1.setId(null);
        assertThat(transactionHistoryDTO1).isNotEqualTo(transactionHistoryDTO2);
    }
}

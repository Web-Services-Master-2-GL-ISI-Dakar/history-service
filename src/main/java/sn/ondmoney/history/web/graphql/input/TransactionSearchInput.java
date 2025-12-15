package sn.ondmoney.history.web.graphql.input;

import sn.ondmoney.history.domain.enumeration.TransactionStatus;
import sn.ondmoney.history.domain.enumeration.TransactionType;
import sn.ondmoney.history.domain.enumeration.TransactionDirection;
import sn.ondmoney.history.domain.enumeration.TransactionSortField;
import sn.ondmoney.history.domain.enumeration.SortDirection;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Setter
@Getter
public class TransactionSearchInput {
    // User identifiers
    private String senderPhone;
    private String receiverPhone;

    // Multi-select filters
    private List<TransactionType> types;
    private List<TransactionStatus> statuses;

    // Date range
    private Instant startDate;
    private Instant endDate;

    // Amount range
    private BigDecimal minAmount;
    private BigDecimal maxAmount;

    // Additional filters
    private String currency;
    private TransactionDirection direction;
    private String merchantCode;
    private String billReference;
    private String bankAccountNumber;
    private String descriptionContains;

    // Pagination & Sorting
    private Integer page = 0;
    private Integer size = 20;
    private TransactionSortField sortBy = TransactionSortField.TRANSACTION_DATE;
    private SortDirection sortDirection = SortDirection.DESC;
}

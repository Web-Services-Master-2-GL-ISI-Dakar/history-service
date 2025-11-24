package sn.ondmoney.history.web.graphql.input;

import sn.ondmoney.history.domain.enumeration.TransactionStatus;
import sn.ondmoney.history.domain.enumeration.TransactionType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Setter
@Getter
public class TransactionSearchInput {
    // Getters et setters
    private String senderPhone;
    private String receiverPhone;
    private TransactionType type;
    private TransactionStatus status;
    private Instant startDate;
    private Instant endDate;
    private BigDecimal minAmount;
    private BigDecimal maxAmount;
    private Integer page;
    private Integer size;

}

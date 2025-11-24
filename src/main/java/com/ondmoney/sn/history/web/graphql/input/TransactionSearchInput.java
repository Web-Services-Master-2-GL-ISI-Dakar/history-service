package com.ondmoney.sn.history.web.graphql.input;

import com.ondmoney.sn.history.domain.enumeration.TransactionStatus;
import com.ondmoney.sn.history.domain.enumeration.TransactionType;
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

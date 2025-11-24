package sn.ondmoney.history.web.graphql.input;

import sn.ondmoney.history.domain.enumeration.TransactionStatus;
import sn.ondmoney.history.domain.enumeration.TransactionType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Setter
@Getter
public class TransactionHistoryInput {
    // Getters et setters pour tous les champs
    private String transactionId;
    private String externalTransactionId;
    private TransactionType type;
    private TransactionStatus status;
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
    private Boolean historySaved;

}

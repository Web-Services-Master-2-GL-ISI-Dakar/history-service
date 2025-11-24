package sn.ondmoney.history.service.dto;

import sn.ondmoney.history.domain.enumeration.TransactionStatus;
import sn.ondmoney.history.domain.enumeration.TransactionType;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

@Setter
@Getter
public class TransactionHistoryDTO implements Serializable {

    private String id;

    @NotNull
    @Field("transaction_id")
    private String transactionId;

    @Field("external_transaction_id")
    private String externalTransactionId;

    @NotNull
    private TransactionType type;

    @NotNull
    private TransactionStatus status;

    @NotNull
    private BigDecimal amount;

    @NotNull
    @Size(max = 3)
    private String currency;

    @NotNull
    @Field("sender_phone")
    private String senderPhone;

    @Field("receiver_phone")
    private String receiverPhone;

    @Field("sender_name")
    private String senderName;

    @Field("receiver_name")
    private String receiverName;

    private String description;

    private BigDecimal fees;

    @Field("balance_before")
    private BigDecimal balanceBefore;

    @Field("balance_after")
    private BigDecimal balanceAfter;

    @Field("merchant_code")
    private String merchantCode;

    @Field("bill_reference")
    private String billReference;

    @Field("bank_account_number")
    private String bankAccountNumber;

    @NotNull
    @Field("transaction_date")
    private Instant transactionDate;

    @Field("processing_date")
    private Instant processingDate;

    @Field("created_by")
    private String createdBy;

    @Field("user_agent")
    private String userAgent;

    @Field("ip_address")
    private String ipAddress;

    @Field("device_id")
    private String deviceId;

    private String metadata;

    @Field("error_message")
    private String errorMessage;

    @Field("correlation_id")
    private String correlationId;

    private Integer version;

    @NotNull
    @Field("history_saved")
    private Boolean historySaved;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TransactionHistoryDTO)) return false;
        if (this.id == null) return false;
        return Objects.equals(this.id, ((TransactionHistoryDTO) o).id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    @Override
    public String toString() {
        return "TransactionHistoryDTO{" +
            "id='" + id + '\'' +
            ", transactionId='" + transactionId + '\'' +
            ", externalTransactionId='" + externalTransactionId + '\'' +
            ", type=" + type +
            ", status=" + status +
            ", amount=" + amount +
            ", currency='" + currency + '\'' +
            ", senderPhone='" + senderPhone + '\'' +
            ", receiverPhone='" + receiverPhone + '\'' +
            ", senderName='" + senderName + '\'' +
            ", receiverName='" + receiverName + '\'' +
            ", description='" + description + '\'' +
            ", fees=" + fees +
            ", balanceBefore=" + balanceBefore +
            ", balanceAfter=" + balanceAfter +
            ", merchantCode='" + merchantCode + '\'' +
            ", billReference='" + billReference + '\'' +
            ", bankAccountNumber='" + bankAccountNumber + '\'' +
            ", transactionDate=" + transactionDate +
            ", processingDate=" + processingDate +
            ", createdBy='" + createdBy + '\'' +
            ", userAgent='" + userAgent + '\'' +
            ", ipAddress='" + ipAddress + '\'' +
            ", deviceId='" + deviceId + '\'' +
            ", metadata='" + metadata + '\'' +
            ", errorMessage='" + errorMessage + '\'' +
            ", correlationId='" + correlationId + '\'' +
            ", version=" + version +
            ", historySaved=" + historySaved +
            '}';
    }
}

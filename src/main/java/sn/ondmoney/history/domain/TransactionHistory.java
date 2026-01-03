package sn.ondmoney.history.domain;

import sn.ondmoney.history.domain.enumeration.TransactionStatus;
import sn.ondmoney.history.domain.enumeration.TransactionType;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * A TransactionHistory.
 */
@Setter
@Getter
@Document(collection = "transaction_history")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "transactionhistory")
public class TransactionHistory implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @NotNull
    @Field("transaction_id")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String transactionId;

    @Field("external_transaction_id")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String externalTransactionId;

    @NotNull
    @Field("type")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private TransactionType type;

    @NotNull
    @Field("status")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private TransactionStatus status;

    @NotNull
    @Field("amount")
    private BigDecimal amount;

    @NotNull
    @Size(max = 3)
    @Field("currency")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String currency;

    @NotNull
    @Field("sender_phone")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String senderPhone;

    @Field("receiver_phone")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String receiverPhone;

    @Field("sender_name")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String senderName;

    @Field("receiver_name")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String receiverName;

    @Field("description")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String description;

    @Field("fees")
    private BigDecimal fees;

    @Field("balance_before")
    private BigDecimal balanceBefore;

    @Field("balance_after")
    private BigDecimal balanceAfter;

    @Field("merchant_code")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String merchantCode;

    @Field("bill_reference")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String billReference;

    @Field("bank_account_number")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String bankAccountNumber;

    @NotNull
    @Field("transaction_date")
    private Instant transactionDate;

    @Field("processing_date")
    private Instant processingDate;

    @Field("created_by")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String createdBy;

    @Field("user_agent")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String userAgent;

    @Field("ip_address")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String ipAddress;

    @Field("device_id")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String deviceId;

    @Field("metadata")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String metadata;

    @Field("error_message")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String errorMessage;

    @Field("correlation_id")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String correlationId;

    @Field("user_id")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private String userId;

    @Field("counterparty_id")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String counterpartyId;

    @Field("counterparty_name")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String counterpartyName;

    @Field("version")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer version;

    @NotNull
    @Field("history_saved")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Boolean)
    private Boolean historySaved;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public TransactionHistory id(String id) {
        this.setId(id);
        return this;
    }

    public TransactionHistory transactionId(String transactionId) {
        this.setTransactionId(transactionId);
        return this;
    }

    public TransactionHistory externalTransactionId(String externalTransactionId) {
        this.setExternalTransactionId(externalTransactionId);
        return this;
    }

    public TransactionHistory type(TransactionType type) {
        this.setType(type);
        return this;
    }

    public TransactionHistory status(TransactionStatus status) {
        this.setStatus(status);
        return this;
    }

    public TransactionHistory amount(BigDecimal amount) {
        this.setAmount(amount);
        return this;
    }

    public TransactionHistory currency(String currency) {
        this.setCurrency(currency);
        return this;
    }

    public TransactionHistory senderPhone(String senderPhone) {
        this.setSenderPhone(senderPhone);
        return this;
    }

    public TransactionHistory receiverPhone(String receiverPhone) {
        this.setReceiverPhone(receiverPhone);
        return this;
    }

    public TransactionHistory senderName(String senderName) {
        this.setSenderName(senderName);
        return this;
    }

    public TransactionHistory receiverName(String receiverName) {
        this.setReceiverName(receiverName);
        return this;
    }

    public TransactionHistory description(String description) {
        this.setDescription(description);
        return this;
    }

    public TransactionHistory fees(BigDecimal fees) {
        this.setFees(fees);
        return this;
    }

    public TransactionHistory balanceBefore(BigDecimal balanceBefore) {
        this.setBalanceBefore(balanceBefore);
        return this;
    }

    public TransactionHistory balanceAfter(BigDecimal balanceAfter) {
        this.setBalanceAfter(balanceAfter);
        return this;
    }

    public TransactionHistory merchantCode(String merchantCode) {
        this.setMerchantCode(merchantCode);
        return this;
    }

    public TransactionHistory billReference(String billReference) {
        this.setBillReference(billReference);
        return this;
    }

    public TransactionHistory bankAccountNumber(String bankAccountNumber) {
        this.setBankAccountNumber(bankAccountNumber);
        return this;
    }

    public TransactionHistory transactionDate(Instant transactionDate) {
        this.setTransactionDate(transactionDate);
        return this;
    }

    public TransactionHistory processingDate(Instant processingDate) {
        this.setProcessingDate(processingDate);
        return this;
    }

    public TransactionHistory createdBy(String createdBy) {
        this.setCreatedBy(createdBy);
        return this;
    }

    public TransactionHistory userAgent(String userAgent) {
        this.setUserAgent(userAgent);
        return this;
    }

    public TransactionHistory ipAddress(String ipAddress) {
        this.setIpAddress(ipAddress);
        return this;
    }

    public TransactionHistory deviceId(String deviceId) {
        this.setDeviceId(deviceId);
        return this;
    }

    public TransactionHistory metadata(String metadata) {
        this.setMetadata(metadata);
        return this;
    }

    public TransactionHistory errorMessage(String errorMessage) {
        this.setErrorMessage(errorMessage);
        return this;
    }

    public TransactionHistory correlationId(String correlationId) {
        this.setCorrelationId(correlationId);
        return this;
    }

    public TransactionHistory version(Integer version) {
        this.setVersion(version);
        return this;
    }

    public TransactionHistory historySaved(Boolean historySaved) {
        this.setHistorySaved(historySaved);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TransactionHistory)) {
            return false;
        }
        return getId() != null && getId().equals(((TransactionHistory) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TransactionHistory{" +
            "id=" + getId() +
            ", transactionId='" + getTransactionId() + "'" +
            ", externalTransactionId='" + getExternalTransactionId() + "'" +
            ", type='" + getType() + "'" +
            ", status='" + getStatus() + "'" +
            ", amount=" + getAmount() +
            ", currency='" + getCurrency() + "'" +
            ", senderPhone='" + getSenderPhone() + "'" +
            ", receiverPhone='" + getReceiverPhone() + "'" +
            ", senderName='" + getSenderName() + "'" +
            ", receiverName='" + getReceiverName() + "'" +
            ", description='" + getDescription() + "'" +
            ", fees=" + getFees() +
            ", balanceBefore=" + getBalanceBefore() +
            ", balanceAfter=" + getBalanceAfter() +
            ", merchantCode='" + getMerchantCode() + "'" +
            ", billReference='" + getBillReference() + "'" +
            ", bankAccountNumber='" + getBankAccountNumber() + "'" +
            ", transactionDate='" + getTransactionDate() + "'" +
            ", processingDate='" + getProcessingDate() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", userAgent='" + getUserAgent() + "'" +
            ", ipAddress='" + getIpAddress() + "'" +
            ", deviceId='" + getDeviceId() + "'" +
            ", metadata='" + getMetadata() + "'" +
            ", errorMessage='" + getErrorMessage() + "'" +
            ", correlationId='" + getCorrelationId() + "'" +
            ", version=" + getVersion() +
            ", historySaved='" + getHistorySaved() + "'" +
            "}";
    }
}

package com.ondmoney.sn.history.domain;

import com.ondmoney.sn.history.domain.enumeration.TransactionStatus;
import com.ondmoney.sn.history.domain.enumeration.TransactionType;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
<<<<<<< HEAD

import lombok.Getter;
import lombok.Setter;
=======
>>>>>>> 74113b81d551b4b03d07d72216f57015d3781672
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * A TransactionHistory.
 */
<<<<<<< HEAD
@Setter
@Getter
=======
>>>>>>> 74113b81d551b4b03d07d72216f57015d3781672
@Document(collection = "transaction_history")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "transactionhistory")
@SuppressWarnings("common-java:DuplicatedBlocks")
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

    @Field("version")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer version;

    @NotNull
    @Field("history_saved")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Boolean)
    private Boolean historySaved;

    // jhipster-needle-entity-add-field - JHipster will add fields here

<<<<<<< HEAD
=======
    public String getId() {
        return this.id;
    }

>>>>>>> 74113b81d551b4b03d07d72216f57015d3781672
    public TransactionHistory id(String id) {
        this.setId(id);
        return this;
    }

<<<<<<< HEAD
=======
    public void setId(String id) {
        this.id = id;
    }

    public String getTransactionId() {
        return this.transactionId;
    }

>>>>>>> 74113b81d551b4b03d07d72216f57015d3781672
    public TransactionHistory transactionId(String transactionId) {
        this.setTransactionId(transactionId);
        return this;
    }

<<<<<<< HEAD
=======
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getExternalTransactionId() {
        return this.externalTransactionId;
    }

>>>>>>> 74113b81d551b4b03d07d72216f57015d3781672
    public TransactionHistory externalTransactionId(String externalTransactionId) {
        this.setExternalTransactionId(externalTransactionId);
        return this;
    }

<<<<<<< HEAD
=======
    public void setExternalTransactionId(String externalTransactionId) {
        this.externalTransactionId = externalTransactionId;
    }

    public TransactionType getType() {
        return this.type;
    }

>>>>>>> 74113b81d551b4b03d07d72216f57015d3781672
    public TransactionHistory type(TransactionType type) {
        this.setType(type);
        return this;
    }

<<<<<<< HEAD
=======
    public void setType(TransactionType type) {
        this.type = type;
    }

    public TransactionStatus getStatus() {
        return this.status;
    }

>>>>>>> 74113b81d551b4b03d07d72216f57015d3781672
    public TransactionHistory status(TransactionStatus status) {
        this.setStatus(status);
        return this;
    }

<<<<<<< HEAD
=======
    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public BigDecimal getAmount() {
        return this.amount;
    }

>>>>>>> 74113b81d551b4b03d07d72216f57015d3781672
    public TransactionHistory amount(BigDecimal amount) {
        this.setAmount(amount);
        return this;
    }

<<<<<<< HEAD
=======
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return this.currency;
    }

>>>>>>> 74113b81d551b4b03d07d72216f57015d3781672
    public TransactionHistory currency(String currency) {
        this.setCurrency(currency);
        return this;
    }

<<<<<<< HEAD
=======
    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getSenderPhone() {
        return this.senderPhone;
    }

>>>>>>> 74113b81d551b4b03d07d72216f57015d3781672
    public TransactionHistory senderPhone(String senderPhone) {
        this.setSenderPhone(senderPhone);
        return this;
    }

<<<<<<< HEAD
=======
    public void setSenderPhone(String senderPhone) {
        this.senderPhone = senderPhone;
    }

    public String getReceiverPhone() {
        return this.receiverPhone;
    }

>>>>>>> 74113b81d551b4b03d07d72216f57015d3781672
    public TransactionHistory receiverPhone(String receiverPhone) {
        this.setReceiverPhone(receiverPhone);
        return this;
    }

<<<<<<< HEAD
=======
    public void setReceiverPhone(String receiverPhone) {
        this.receiverPhone = receiverPhone;
    }

    public String getSenderName() {
        return this.senderName;
    }

>>>>>>> 74113b81d551b4b03d07d72216f57015d3781672
    public TransactionHistory senderName(String senderName) {
        this.setSenderName(senderName);
        return this;
    }

<<<<<<< HEAD
=======
    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getReceiverName() {
        return this.receiverName;
    }

>>>>>>> 74113b81d551b4b03d07d72216f57015d3781672
    public TransactionHistory receiverName(String receiverName) {
        this.setReceiverName(receiverName);
        return this;
    }

<<<<<<< HEAD
=======
    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getDescription() {
        return this.description;
    }

>>>>>>> 74113b81d551b4b03d07d72216f57015d3781672
    public TransactionHistory description(String description) {
        this.setDescription(description);
        return this;
    }

<<<<<<< HEAD
=======
    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getFees() {
        return this.fees;
    }

>>>>>>> 74113b81d551b4b03d07d72216f57015d3781672
    public TransactionHistory fees(BigDecimal fees) {
        this.setFees(fees);
        return this;
    }

<<<<<<< HEAD
=======
    public void setFees(BigDecimal fees) {
        this.fees = fees;
    }

    public BigDecimal getBalanceBefore() {
        return this.balanceBefore;
    }

>>>>>>> 74113b81d551b4b03d07d72216f57015d3781672
    public TransactionHistory balanceBefore(BigDecimal balanceBefore) {
        this.setBalanceBefore(balanceBefore);
        return this;
    }

<<<<<<< HEAD
=======
    public void setBalanceBefore(BigDecimal balanceBefore) {
        this.balanceBefore = balanceBefore;
    }

    public BigDecimal getBalanceAfter() {
        return this.balanceAfter;
    }

>>>>>>> 74113b81d551b4b03d07d72216f57015d3781672
    public TransactionHistory balanceAfter(BigDecimal balanceAfter) {
        this.setBalanceAfter(balanceAfter);
        return this;
    }

<<<<<<< HEAD
=======
    public void setBalanceAfter(BigDecimal balanceAfter) {
        this.balanceAfter = balanceAfter;
    }

    public String getMerchantCode() {
        return this.merchantCode;
    }

>>>>>>> 74113b81d551b4b03d07d72216f57015d3781672
    public TransactionHistory merchantCode(String merchantCode) {
        this.setMerchantCode(merchantCode);
        return this;
    }

<<<<<<< HEAD
=======
    public void setMerchantCode(String merchantCode) {
        this.merchantCode = merchantCode;
    }

    public String getBillReference() {
        return this.billReference;
    }

>>>>>>> 74113b81d551b4b03d07d72216f57015d3781672
    public TransactionHistory billReference(String billReference) {
        this.setBillReference(billReference);
        return this;
    }

<<<<<<< HEAD
=======
    public void setBillReference(String billReference) {
        this.billReference = billReference;
    }

    public String getBankAccountNumber() {
        return this.bankAccountNumber;
    }

>>>>>>> 74113b81d551b4b03d07d72216f57015d3781672
    public TransactionHistory bankAccountNumber(String bankAccountNumber) {
        this.setBankAccountNumber(bankAccountNumber);
        return this;
    }

<<<<<<< HEAD
=======
    public void setBankAccountNumber(String bankAccountNumber) {
        this.bankAccountNumber = bankAccountNumber;
    }

    public Instant getTransactionDate() {
        return this.transactionDate;
    }

>>>>>>> 74113b81d551b4b03d07d72216f57015d3781672
    public TransactionHistory transactionDate(Instant transactionDate) {
        this.setTransactionDate(transactionDate);
        return this;
    }

<<<<<<< HEAD
=======
    public void setTransactionDate(Instant transactionDate) {
        this.transactionDate = transactionDate;
    }

    public Instant getProcessingDate() {
        return this.processingDate;
    }

>>>>>>> 74113b81d551b4b03d07d72216f57015d3781672
    public TransactionHistory processingDate(Instant processingDate) {
        this.setProcessingDate(processingDate);
        return this;
    }

<<<<<<< HEAD
=======
    public void setProcessingDate(Instant processingDate) {
        this.processingDate = processingDate;
    }

    public String getCreatedBy() {
        return this.createdBy;
    }

>>>>>>> 74113b81d551b4b03d07d72216f57015d3781672
    public TransactionHistory createdBy(String createdBy) {
        this.setCreatedBy(createdBy);
        return this;
    }

<<<<<<< HEAD
=======
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUserAgent() {
        return this.userAgent;
    }

>>>>>>> 74113b81d551b4b03d07d72216f57015d3781672
    public TransactionHistory userAgent(String userAgent) {
        this.setUserAgent(userAgent);
        return this;
    }

<<<<<<< HEAD
=======
    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getIpAddress() {
        return this.ipAddress;
    }

>>>>>>> 74113b81d551b4b03d07d72216f57015d3781672
    public TransactionHistory ipAddress(String ipAddress) {
        this.setIpAddress(ipAddress);
        return this;
    }

<<<<<<< HEAD
=======
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getDeviceId() {
        return this.deviceId;
    }

>>>>>>> 74113b81d551b4b03d07d72216f57015d3781672
    public TransactionHistory deviceId(String deviceId) {
        this.setDeviceId(deviceId);
        return this;
    }

<<<<<<< HEAD
=======
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getMetadata() {
        return this.metadata;
    }

>>>>>>> 74113b81d551b4b03d07d72216f57015d3781672
    public TransactionHistory metadata(String metadata) {
        this.setMetadata(metadata);
        return this;
    }

<<<<<<< HEAD
=======
    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

>>>>>>> 74113b81d551b4b03d07d72216f57015d3781672
    public TransactionHistory errorMessage(String errorMessage) {
        this.setErrorMessage(errorMessage);
        return this;
    }

<<<<<<< HEAD
=======
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getCorrelationId() {
        return this.correlationId;
    }

>>>>>>> 74113b81d551b4b03d07d72216f57015d3781672
    public TransactionHistory correlationId(String correlationId) {
        this.setCorrelationId(correlationId);
        return this;
    }

<<<<<<< HEAD
=======
    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public Integer getVersion() {
        return this.version;
    }

>>>>>>> 74113b81d551b4b03d07d72216f57015d3781672
    public TransactionHistory version(Integer version) {
        this.setVersion(version);
        return this;
    }

<<<<<<< HEAD
=======
    public void setVersion(Integer version) {
        this.version = version;
    }

    public Boolean getHistorySaved() {
        return this.historySaved;
    }

>>>>>>> 74113b81d551b4b03d07d72216f57015d3781672
    public TransactionHistory historySaved(Boolean historySaved) {
        this.setHistorySaved(historySaved);
        return this;
    }

<<<<<<< HEAD
=======
    public void setHistorySaved(Boolean historySaved) {
        this.historySaved = historySaved;
    }

>>>>>>> 74113b81d551b4b03d07d72216f57015d3781672
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

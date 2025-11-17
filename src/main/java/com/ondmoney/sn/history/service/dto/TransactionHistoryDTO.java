package com.ondmoney.sn.history.service.dto;

import com.ondmoney.sn.history.domain.enumeration.TransactionStatus;
import com.ondmoney.sn.history.domain.enumeration.TransactionType;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.ondmoney.sn.history.domain.TransactionHistory} entity.
 */
public class TransactionHistoryDTO implements Serializable {

    private String id;

    @NotNull
    private String transactionId;

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

    @NotNull
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

    @NotNull
    private Boolean historySaved;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getExternalTransactionId() {
        return externalTransactionId;
    }

    public void setExternalTransactionId(String externalTransactionId) {
        this.externalTransactionId = externalTransactionId;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getSenderPhone() {
        return senderPhone;
    }

    public void setSenderPhone(String senderPhone) {
        this.senderPhone = senderPhone;
    }

    public String getReceiverPhone() {
        return receiverPhone;
    }

    public void setReceiverPhone(String receiverPhone) {
        this.receiverPhone = receiverPhone;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getFees() {
        return fees;
    }

    public void setFees(BigDecimal fees) {
        this.fees = fees;
    }

    public BigDecimal getBalanceBefore() {
        return balanceBefore;
    }

    public void setBalanceBefore(BigDecimal balanceBefore) {
        this.balanceBefore = balanceBefore;
    }

    public BigDecimal getBalanceAfter() {
        return balanceAfter;
    }

    public void setBalanceAfter(BigDecimal balanceAfter) {
        this.balanceAfter = balanceAfter;
    }

    public String getMerchantCode() {
        return merchantCode;
    }

    public void setMerchantCode(String merchantCode) {
        this.merchantCode = merchantCode;
    }

    public String getBillReference() {
        return billReference;
    }

    public void setBillReference(String billReference) {
        this.billReference = billReference;
    }

    public String getBankAccountNumber() {
        return bankAccountNumber;
    }

    public void setBankAccountNumber(String bankAccountNumber) {
        this.bankAccountNumber = bankAccountNumber;
    }

    public Instant getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Instant transactionDate) {
        this.transactionDate = transactionDate;
    }

    public Instant getProcessingDate() {
        return processingDate;
    }

    public void setProcessingDate(Instant processingDate) {
        this.processingDate = processingDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Boolean getHistorySaved() {
        return historySaved;
    }

    public void setHistorySaved(Boolean historySaved) {
        this.historySaved = historySaved;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TransactionHistoryDTO)) {
            return false;
        }

        TransactionHistoryDTO transactionHistoryDTO = (TransactionHistoryDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, transactionHistoryDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TransactionHistoryDTO{" +
            "id='" + getId() + "'" +
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

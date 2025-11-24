package sn.ondmoney.history.service.dto;

import sn.ondmoney.history.domain.enumeration.HistoryEventType;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

public class HistoryEventDTO implements Serializable {

    private HistoryEventType eventType;
    private String transactionId;
    private Instant timestamp;
    private String message;
    private String correlationId;
    private Object additionalData;

    public HistoryEventDTO() {}

    public HistoryEventDTO(
        HistoryEventType eventType,
        String transactionId,
        Instant timestamp,
        String message,
        String correlationId,
        Object additionalData
    ) {
        this.eventType = eventType;
        this.transactionId = transactionId;
        this.timestamp = timestamp;
        this.message = message;
        this.correlationId = correlationId;
        this.additionalData = additionalData;
    }

    public HistoryEventType getEventType() {
        return eventType;
    }

    public void setEventType(HistoryEventType eventType) {
        this.eventType = eventType;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public Object getAdditionalData() {
        return additionalData;
    }

    public void setAdditionalData(Object additionalData) {
        this.additionalData = additionalData;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof HistoryEventDTO)) {
            return false;
        }
        HistoryEventDTO that = (HistoryEventDTO) o;
        return (
            Objects.equals(transactionId, that.transactionId) &&
            eventType == that.eventType &&
            Objects.equals(timestamp, that.timestamp) &&
            Objects.equals(correlationId, that.correlationId)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventType, transactionId, timestamp, correlationId);
    }

    @Override
    public String toString() {
        return (
            "HistoryEventDTO{" +
            "eventType=" +
            eventType +
            ", transactionId='" +
            transactionId +
            '\'' +
            ", timestamp=" +
            timestamp +
            ", message='" +
            message +
            '\'' +
            ", correlationId='" +
            correlationId +
            '\'' +
            ", additionalData=" +
            additionalData +
            '}'
        );
    }
}

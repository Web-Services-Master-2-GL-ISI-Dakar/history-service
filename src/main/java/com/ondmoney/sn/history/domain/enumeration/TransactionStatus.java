package com.ondmoney.sn.history.domain.enumeration;

<<<<<<< HEAD
import lombok.Getter;

/**
 * The TransactionStatus enumeration.
 */
@Getter
=======
/**
 * The TransactionStatus enumeration.
 */
>>>>>>> 74113b81d551b4b03d07d72216f57015d3781672
public enum TransactionStatus {
    PENDING("En attente"),
    SUCCESS("Réussi"),
    FAILED("Échoué"),
    CANCELLED("Annulé"),
    PROCESSING("En cours de traitement");

    private final String description;

    TransactionStatus(String description) {
        this.description = description;
    }

<<<<<<< HEAD
=======
    public String getDescription() {
        return description;
    }
>>>>>>> 74113b81d551b4b03d07d72216f57015d3781672
}

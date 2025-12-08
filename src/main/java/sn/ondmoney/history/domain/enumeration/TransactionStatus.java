package sn.ondmoney.history.domain.enumeration;

import lombok.Getter;

/**
 * The TransactionStatus enumeration.
 */
@Getter
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

}

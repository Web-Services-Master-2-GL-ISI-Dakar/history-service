package com.ondmoney.sn.history.domain.enumeration;

/**
 * The TransactionStatus enumeration.
 */
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

    public String getDescription() {
        return description;
    }
}

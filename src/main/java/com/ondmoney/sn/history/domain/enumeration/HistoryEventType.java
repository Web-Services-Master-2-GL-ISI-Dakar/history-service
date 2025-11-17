package com.ondmoney.sn.history.domain.enumeration;

/**
 * The TransactionType enumeration.
 */
public enum HistoryEventType {
    HISTORY_SAVED("Historique sauvegardé"),
    HISTORY_PROCESSED("Historique traité"),
    HISTORY_FAILED("Échec de sauvegarde"),
    RECONCILIATION_NEEDED("Besoin de réconciliation");

    private final String description;

    HistoryEventType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

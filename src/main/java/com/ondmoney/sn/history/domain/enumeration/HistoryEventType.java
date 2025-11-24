package com.ondmoney.sn.history.domain.enumeration;

import lombok.Getter;

/**
 * The TransactionType enumeration.
 */
@Getter
public enum HistoryEventType {
    HISTORY_SAVED("Historique sauvegardé"),
    HISTORY_PROCESSED("Historique traité"),
    HISTORY_FAILED("Échec de sauvegarde"),
    RECONCILIATION_NEEDED("Besoin de réconciliation");

    private final String description;

    HistoryEventType(String description) {
        this.description = description;
    }

}

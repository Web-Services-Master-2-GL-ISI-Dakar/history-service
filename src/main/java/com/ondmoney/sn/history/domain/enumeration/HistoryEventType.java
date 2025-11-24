package com.ondmoney.sn.history.domain.enumeration;

<<<<<<< HEAD
import lombok.Getter;

/**
 * The TransactionType enumeration.
 */
@Getter
=======
/**
 * The TransactionType enumeration.
 */
>>>>>>> 74113b81d551b4b03d07d72216f57015d3781672
public enum HistoryEventType {
    HISTORY_SAVED("Historique sauvegardé"),
    HISTORY_PROCESSED("Historique traité"),
    HISTORY_FAILED("Échec de sauvegarde"),
    RECONCILIATION_NEEDED("Besoin de réconciliation");

    private final String description;

    HistoryEventType(String description) {
        this.description = description;
    }

<<<<<<< HEAD
=======
    public String getDescription() {
        return description;
    }
>>>>>>> 74113b81d551b4b03d07d72216f57015d3781672
}

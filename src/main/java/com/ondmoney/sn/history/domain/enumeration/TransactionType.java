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
public enum TransactionType {
    DEPOSIT("Dépôt d'argent"),
    WITHDRAWAL("Retrait d'argent"),
    TRANSFER("Transfert d'argent"),
    BILL_PAYMENT("Paiement de facture"),
    AIRTIME("Achat de crédit"),
    MERCHANT_PAYMENT("Paiement marchand"),
    BANK_TRANSFER("Transfert bancaire"),
    TOP_UP_CARD("Recharge de carte");

    private final String description;

    TransactionType(String description) {
        this.description = description;
    }

<<<<<<< HEAD
=======
    public String getDescription() {
        return description;
    }
>>>>>>> 74113b81d551b4b03d07d72216f57015d3781672
}

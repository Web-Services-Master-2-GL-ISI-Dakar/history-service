package com.ondmoney.sn.history.domain.enumeration;

/**
 * The TransactionType enumeration.
 */
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

    public String getDescription() {
        return description;
    }
}

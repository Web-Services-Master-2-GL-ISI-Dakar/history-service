package sn.ondmoney.history.domain.enumeration;

import lombok.Getter;

/**
 * The TransactionType enumeration.
 */
@Getter
public enum TransactionType {
    DEPOSIT("Dépôt d'argent"),
    WITHDRAWAL("Retrait d'argent"),
    TRANSFER("Transfert d'argent"),
    BILL_PAYMENT("Paiement de facture"),
    AIRTIME("Achat de crédit"),
    MERCHANT_PAYMENT("Paiement marchand"),
    BANK_TRANSFER("Transfert bancaire"),
    TOP_UP_CARD("Recharge de carte"),
    WALLET_CREATION("Création de portefeuille");

    private final String description;

    TransactionType(String description) {
        this.description = description;
    }

}

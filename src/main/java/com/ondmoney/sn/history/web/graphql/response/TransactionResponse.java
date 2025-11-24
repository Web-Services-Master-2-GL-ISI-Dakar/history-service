package com.ondmoney.sn.history.web.graphql.response;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TransactionResponse {
    // Getters et setters
    private String status;
    private String message;
    private String type;

    // Constructeurs
    public TransactionResponse() {
    }

    public TransactionResponse(String status, String message) {
        this.status = status;
        this.message = message;
    }

    public TransactionResponse(String status, String message, String type) {
        this.status = status;
        this.message = message;
        this.type = type;
    }

    @Override
    public String toString() {
        return "TransactionResponse{" +
            "status='" + status + '\'' +
            ", message='" + message + '\'' +
            ", type='" + type + '\'' +
            '}';
    }
}

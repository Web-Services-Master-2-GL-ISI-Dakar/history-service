package sn.ondmoney.history.web.graphql.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class TransactionTypesResponse {
    // Getters et setters
    private List<String> types;

    public TransactionTypesResponse(List<String> types) {
        this.types = types;
    }

}

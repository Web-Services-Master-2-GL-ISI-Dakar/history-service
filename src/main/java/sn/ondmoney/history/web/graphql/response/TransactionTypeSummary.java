package sn.ondmoney.history.web.graphql.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sn.ondmoney.history.domain.enumeration.TransactionType;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionTypeSummary {
    private TransactionType type;
    private int count;
    private BigDecimal totalAmount;
    private float percentage;
}

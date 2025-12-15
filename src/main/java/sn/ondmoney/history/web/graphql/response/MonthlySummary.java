package sn.ondmoney.history.web.graphql.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonthlySummary {
    private String month; // Format: "YYYY-MM"
    private int count;
    private BigDecimal totalAmount;
}

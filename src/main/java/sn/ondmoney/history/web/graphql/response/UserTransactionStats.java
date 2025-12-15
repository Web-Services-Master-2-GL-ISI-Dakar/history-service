package sn.ondmoney.history.web.graphql.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserTransactionStats {
    private int totalTransactions;
    private BigDecimal totalAmount;
    private int successfulTransactions;
    private int failedTransactions;
    private int pendingTransactions;
    private int cancelledTransactions;
    private int processingTransactions;

    private List<TransactionTypeSummary> transactionTypeSummary;

    private Instant firstTransactionDate;
    private Instant lastTransactionDate;
    private Instant periodStart;
    private Instant periodEnd;

    private List<MonthlySummary> monthlySummary;
}

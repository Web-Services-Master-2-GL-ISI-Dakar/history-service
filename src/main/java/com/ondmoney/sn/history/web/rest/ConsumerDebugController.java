package com.ondmoney.sn.history.web.rest;

import com.ondmoney.sn.history.repository.TransactionHistoryRepository;
import com.ondmoney.sn.history.repository.search.TransactionHistorySearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/debug")
public class ConsumerDebugController {

    private final Logger log = LoggerFactory.getLogger(ConsumerDebugController.class);
    private final TransactionHistoryRepository transactionHistoryRepository;
    private final TransactionHistorySearchRepository transactionHistorySearchRepository;

    public ConsumerDebugController(
        TransactionHistoryRepository transactionHistoryRepository,
        TransactionHistorySearchRepository transactionHistorySearchRepository
    ) {
        this.transactionHistoryRepository = transactionHistoryRepository;
        this.transactionHistorySearchRepository = transactionHistorySearchRepository;
    }

    @GetMapping("/count")
    public ResponseEntity<String> getCounts() {
        long mongoCount = transactionHistoryRepository.count();
        long esCount = transactionHistorySearchRepository.count();

        String result = String.format(
            "MongoDB: %d documents | Elasticsearch: %d documents | Diff: %d",
            mongoCount,
            esCount,
            mongoCount - esCount
        );

        log.info("Debug counts - {}", result);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/recent")
    public ResponseEntity<?> getRecentTransactions() {
        var transactions = transactionHistoryRepository.findAll().stream().limit(5).toList();

        return ResponseEntity.ok(transactions);
    }

    @PostMapping("/reindex")
    public ResponseEntity<String> reindexAll() {
        log.info("Manual reindexing all documents...");

        var allTransactions = transactionHistoryRepository.findAll();
        transactionHistorySearchRepository.deleteAll();

        int count = 0;
        for (var transaction : allTransactions) {
            transactionHistorySearchRepository.save(transaction);
            count++;
        }

        String result = String.format("Reindexed %d documents to Elasticsearch", count);
        log.info(result);
        return ResponseEntity.ok(result);
    }
}

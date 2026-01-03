package sn.ondmoney.history.web.rest;

import sn.ondmoney.history.domain.enumeration.TransactionType;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test")
public class TransactionEventController {

    private static final Logger LOG = LoggerFactory.getLogger(TransactionEventController.class);

    private final TransactionTestDataGenerator testDataGenerator;

    public TransactionEventController(TransactionTestDataGenerator testDataGenerator) {
        this.testDataGenerator = testDataGenerator;
    }

    /**
     * Génère un type de transaction spécifique
     */
    @PostMapping("/transactions/{type}")
    public ResponseEntity<Map<String, String>> generateTransaction(@PathVariable TransactionType type) {
        LOG.info("Generating test transaction of type: {}", type);
        testDataGenerator.generateTestTransaction(type);

        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Transaction " + type + " generated and sent to Kafka");
        response.put("type", type.toString());

        return ResponseEntity.ok(response);
    }

    /**
     * Génère tous les types de transactions
     */
    @PostMapping("/transactions/all")
    public ResponseEntity<Map<String, Object>> generateAllTransactions() {
        LOG.info("Generating all transaction types");

        List<String> results = Arrays.stream(TransactionType.values())
            .map(type -> {
                testDataGenerator.generateTestTransaction(type);
                return type.toString() + " - Generated";
            })
            .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "All transaction types generated and sent to Kafka");
        response.put("generated", results);

        return ResponseEntity.ok(response);
    }

    /**
     * Liste tous les types de transactions disponibles
     */
    @GetMapping("/transaction-types")
    public ResponseEntity<List<String>> getTransactionTypes() {
        List<String> types = Arrays.stream(TransactionType.values()).map(Enum::toString).collect(Collectors.toList());

        return ResponseEntity.ok(types);
    }
}

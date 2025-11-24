package com.ondmoney.sn.history.repository;

import com.ondmoney.sn.history.domain.TransactionHistory;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for TransactionHistory entity
 */
@Repository
public interface TransactionHistoryRepository extends MongoRepository<TransactionHistory, String> {

    boolean existsByTransactionId(String transactionId);

}

package sn.ondmoney.history.repository;

import sn.ondmoney.history.domain.TransactionHistory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for TransactionHistory entity
 */
@Repository
public interface TransactionHistoryRepository extends MongoRepository<TransactionHistory, String> {

    boolean existsByTransactionId(String transactionId);

}

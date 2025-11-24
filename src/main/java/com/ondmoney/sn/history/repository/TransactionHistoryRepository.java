package com.ondmoney.sn.history.repository;

import com.ondmoney.sn.history.domain.TransactionHistory;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
<<<<<<< HEAD
 * Spring Data MongoDB repository for TransactionHistory entity
 */
@Repository
public interface TransactionHistoryRepository extends MongoRepository<TransactionHistory, String> {

    boolean existsByTransactionId(String transactionId);

}
=======
 * Spring Data MongoDB repository for the TransactionHistory entity.
 */
@Repository
public interface TransactionHistoryRepository extends MongoRepository<TransactionHistory, String> {}
>>>>>>> 74113b81d551b4b03d07d72216f57015d3781672

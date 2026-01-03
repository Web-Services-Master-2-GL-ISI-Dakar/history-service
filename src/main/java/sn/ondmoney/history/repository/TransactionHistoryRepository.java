package sn.ondmoney.history.repository;

import sn.ondmoney.history.domain.TransactionHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for TransactionHistory entity
 */
@Repository
public interface TransactionHistoryRepository extends MongoRepository<TransactionHistory, String> {

    boolean existsByTransactionId(String transactionId);

    // Find transactions where user is sender
    Page<TransactionHistory> findBySenderPhone(String senderPhone, Pageable pageable);

    // Find transactions where user is receiver
    Page<TransactionHistory> findByReceiverPhone(String receiverPhone, Pageable pageable);

    // Find transactions where user is either sender or receiver
    @Query("{ $or: [ { sender_phone: ?0 }, { receiver_phone: ?0 } ] }")
    Page<TransactionHistory> findByUserPhone(String phoneNumber, Pageable pageable);

}

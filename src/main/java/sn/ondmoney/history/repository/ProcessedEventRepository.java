package sn.ondmoney.history.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import sn.ondmoney.history.domain.ProcessedEvent;

/**
 * Repository for ProcessedEvent - tracks processed Kafka events for idempotency.
 */
@Repository
public interface ProcessedEventRepository extends MongoRepository<ProcessedEvent, String> {

    boolean existsByEventId(String eventId);
}

package sn.ondmoney.history.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import sn.ondmoney.history.domain.TransactionHistory;
import sn.ondmoney.history.domain.enumeration.TransactionDirection;
import sn.ondmoney.history.domain.enumeration.TransactionStatus;
import sn.ondmoney.history.domain.enumeration.TransactionType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * Service for searching transactions in MongoDB
 */
@Service
public class TransactionHistoryMongoSearchService {

    private static final Logger LOG = LoggerFactory.getLogger(TransactionHistoryMongoSearchService.class);

    private final MongoTemplate mongoTemplate;

    public TransactionHistoryMongoSearchService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public Page<TransactionHistory> searchTransactions(
        String senderPhone,
        String receiverPhone,
        List<TransactionType> types,
        List<TransactionStatus> statuses,
        Instant startDate,
        Instant endDate,
        BigDecimal minAmount,
        BigDecimal maxAmount,
        String currency,
        TransactionDirection direction,
        String merchantCode,
        String billReference,
        String bankAccountNumber,
        String descriptionContains,
        Pageable pageable
    ) {
        LOG.debug(
            "MongoDB search - sender: {}, receiver: {}, types: {}, statuses: {}, direction: {}",
            senderPhone,
            receiverPhone,
            types,
            statuses,
            direction
        );

        Query query = new Query();
        Criteria criteria = new Criteria();

        // Handle phone number filtering based on direction
        if (direction != null) {
            switch (direction) {
                case SENT:
                    if (senderPhone != null) {
                        query.addCriteria(Criteria.where("sender_phone").is(senderPhone));
                    }
                    break;
                case RECEIVED:
                    if (receiverPhone != null) {
                        query.addCriteria(Criteria.where("receiver_phone").is(receiverPhone));
                    }
                    break;
                case ALL:
                    // For ALL, search both sender and receiver
                    if (senderPhone != null) {
                        query.addCriteria(new Criteria().orOperator(
                            Criteria.where("sender_phone").is(senderPhone),
                            Criteria.where("receiver_phone").is(senderPhone)
                        ));
                    } else if (receiverPhone != null) {
                        query.addCriteria(new Criteria().orOperator(
                            Criteria.where("sender_phone").is(receiverPhone),
                            Criteria.where("receiver_phone").is(receiverPhone)
                        ));
                    }
                    break;
            }
        } else {
            // No direction specified
            if (senderPhone != null) {
                query.addCriteria(Criteria.where("sender_phone").is(senderPhone));
            }
            if (receiverPhone != null) {
                query.addCriteria(Criteria.where("receiver_phone").is(receiverPhone));
            }
        }

        // Transaction types
        if (types != null && !types.isEmpty()) {
            query.addCriteria(Criteria.where("type").in(types));
        }

        // Transaction statuses
        if (statuses != null && !statuses.isEmpty()) {
            query.addCriteria(Criteria.where("status").in(statuses));
        }

        // Date range
        if (startDate != null || endDate != null) {
            Criteria dateCriteria = Criteria.where("transaction_date");
            if (startDate != null) {
                dateCriteria = dateCriteria.gte(startDate);
            }
            if (endDate != null) {
                dateCriteria = dateCriteria.lte(endDate);
            }
            query.addCriteria(dateCriteria);
        }

        // Amount range
        if (minAmount != null || maxAmount != null) {
            Criteria amountCriteria = Criteria.where("amount");
            if (minAmount != null) {
                amountCriteria = amountCriteria.gte(minAmount.toString());
            }
            if (maxAmount != null) {
                amountCriteria = amountCriteria.lte(maxAmount.toString());
            }
            query.addCriteria(amountCriteria);
        }

        // Currency
        if (currency != null) {
            query.addCriteria(Criteria.where("currency").is(currency));
        }

        // Merchant code
        if (merchantCode != null) {
            query.addCriteria(Criteria.where("merchant_code").is(merchantCode));
        }

        // Bill reference
        if (billReference != null) {
            query.addCriteria(Criteria.where("bill_reference").is(billReference));
        }

        // Bank account number
        if (bankAccountNumber != null) {
            query.addCriteria(Criteria.where("bank_account_number").is(bankAccountNumber));
        }

        // Description contains
        if (descriptionContains != null) {
            query.addCriteria(Criteria.where("description").regex(descriptionContains, "i"));
        }

        // Add pagination and sorting
        query.with(pageable);

        LOG.debug("MongoDB query: {}", query);

        // Execute query
        List<TransactionHistory> results = mongoTemplate.find(query, TransactionHistory.class);
        long total = mongoTemplate.count(query.skip(-1).limit(-1), TransactionHistory.class);

        return new PageImpl<>(results, pageable, total);
    }
}

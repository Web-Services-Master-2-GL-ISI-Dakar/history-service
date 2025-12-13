package sn.ondmoney.history.service.mapper;

import sn.ondmoney.history.domain.TransactionHistory;
import sn.ondmoney.history.domain.enumeration.TransactionStatus;
import sn.ondmoney.history.domain.enumeration.TransactionType;
import sn.ondmoney.history.service.dto.TransactionHistoryDTO;
import org.mapstruct.Mapper;

import java.math.BigDecimal;
import java.time.Instant;

@Mapper(componentModel = "spring")
public interface TransactionHistoryMapper extends EntityMapper<TransactionHistoryDTO, TransactionHistory> {

    @Override
    default TransactionHistoryDTO toDto(TransactionHistory entity) {
        if (entity == null) return null;

        TransactionHistoryDTO dto = new TransactionHistoryDTO();

        dto.setId(entity.getId());
        dto.setTransactionId(entity.getTransactionId());
        dto.setExternalTransactionId(entity.getExternalTransactionId());
        dto.setType(entity.getType());
        dto.setStatus(entity.getStatus());
        dto.setAmount(entity.getAmount());
        dto.setCurrency(entity.getCurrency());
        dto.setSenderPhone(entity.getSenderPhone());
        dto.setReceiverPhone(entity.getReceiverPhone());
        dto.setSenderName(entity.getSenderName());
        dto.setReceiverName(entity.getReceiverName());
        dto.setDescription(entity.getDescription());
        dto.setFees(entity.getFees());
        dto.setBalanceBefore(entity.getBalanceBefore());
        dto.setBalanceAfter(entity.getBalanceAfter());
        dto.setMerchantCode(entity.getMerchantCode());
        dto.setBillReference(entity.getBillReference());
        dto.setBankAccountNumber(entity.getBankAccountNumber());
        dto.setTransactionDate(entity.getTransactionDate());
        dto.setProcessingDate(entity.getProcessingDate());
        dto.setCreatedBy(entity.getCreatedBy());
        dto.setUserAgent(entity.getUserAgent());
        dto.setIpAddress(entity.getIpAddress());
        dto.setDeviceId(entity.getDeviceId());
        dto.setMetadata(entity.getMetadata());
        dto.setErrorMessage(entity.getErrorMessage());
        dto.setCorrelationId(entity.getCorrelationId());
        dto.setVersion(entity.getVersion());
        dto.setHistorySaved(entity.getHistorySaved());

        return dto;
    }
}

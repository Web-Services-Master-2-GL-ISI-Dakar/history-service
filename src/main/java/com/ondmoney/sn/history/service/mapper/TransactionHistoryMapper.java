package com.ondmoney.sn.history.service.mapper;

import com.ondmoney.sn.history.domain.TransactionHistory;
import com.ondmoney.sn.history.service.dto.TransactionHistoryDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link TransactionHistory} and its DTO {@link TransactionHistoryDTO}.
 */
@Mapper(componentModel = "spring")
public interface TransactionHistoryMapper extends EntityMapper<TransactionHistoryDTO, TransactionHistory> {}

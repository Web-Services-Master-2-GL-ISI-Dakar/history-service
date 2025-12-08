package sn.ondmoney.history.web.graphql.response;

import sn.ondmoney.history.service.dto.TransactionHistoryDTO;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;

@Setter
@Getter
public class TransactionPageResponse {
    // Getters et setters
    private List<TransactionHistoryDTO> content;
    private Long totalElements;
    private Integer totalPages;
    private Integer size;
    private Integer number;

    public TransactionPageResponse(List<TransactionHistoryDTO> content, Long totalElements, Integer totalPages, Integer size, Integer number) {
        this.content = content;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.size = size;
        this.number = number;
    }

    public static TransactionPageResponse from(Page<TransactionHistoryDTO> page) {
        return new TransactionPageResponse(
            page.getContent(),
            page.getTotalElements(),
            page.getTotalPages(),
            page.getSize(),
            page.getNumber()
        );
    }

}

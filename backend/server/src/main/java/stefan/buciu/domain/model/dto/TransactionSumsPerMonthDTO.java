package stefan.buciu.domain.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionSumsPerMonthDTO {
    private int month;
    private BigDecimal income;
    private BigDecimal expense;
}

package stefan.buciu.domain.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import stefan.buciu.domain.model.Account;
import stefan.buciu.domain.model.Transaction;
import stefan.buciu.domain.model.TransactionType;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDTO implements DTO<Transaction, Long> {

    @ApiModelProperty(example = "1", value = "id")
    private long id;

    @ApiModelProperty(required = true, example = "1234.56", value = "value")
    @DecimalMin(value = "0.0", inclusive = false)
    @Digits(integer = 10, fraction = 2)
    @NotNull(message = "Value cannot be null.")
    private BigDecimal value;

    @ApiModelProperty(required = true, example = "INCOME", value = "type", dataType = "TransactionType")
    private TransactionType type;

    @ApiModelProperty(example = "2021-05-07T00:15:30Z", value = "date")
    private LocalDateTime date;

    public TransactionDTO(Transaction transaction) {
        this.id = transaction.getId();
        this.value = transaction.getValue();
        this.type = transaction.getType();
        this.date = transaction.getDate();
    }

    @Override
    public Transaction toEntity() {
        Transaction transaction = new Transaction();
        transaction.setValue(this.value);
        transaction.setType(this.type);
        transaction.setDate(this.date);
        return transaction;
    }
}

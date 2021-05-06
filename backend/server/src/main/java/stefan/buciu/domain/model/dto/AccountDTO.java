package stefan.buciu.domain.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import stefan.buciu.domain.model.Account;
import stefan.buciu.domain.model.CurrencyType;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountDTO implements DTO<Account, Long> {

    @ApiModelProperty(example = "1", value = "id")
    private long id;

    @ApiModelProperty(required = true, example = "1234.56", value = "money")
    @DecimalMin(value = "0.0", inclusive = false)
    @Digits(integer = 10, fraction = 2)
    @NotNull(message = "Money cannot be null.")
    private BigDecimal money;

    @ApiModelProperty(required = true, example = "1234.56", value = "monthlyIncome")
    @DecimalMin(value = "0.0", inclusive = false)
    @Digits(integer = 10, fraction = 2)
    private BigDecimal monthlyIncome;

    @ApiModelProperty(required = true, example = "1234.56", value = "monthlyIncome", dataType = "CurrencyType")
    private CurrencyType currency;

    public AccountDTO(Account account) {
        this.id = account.getId();
        this.money = account.getMoney();
        this.monthlyIncome = account.getMonthlyIncome();
        this.currency = account.getCurrency();
    }

    @Override
    public Account toEntity() {
        Account account = new Account();
        account.setId(this.id);
        account.setMoney(this.money);
        account.setMonthlyIncome(this.monthlyIncome);
        account.setCurrency(this.currency);
        return account;
    }
}

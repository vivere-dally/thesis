package stefan.buciu.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@javax.persistence.Entity
@javax.persistence.Table(name = "accounts")
public class Account implements Entity<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Version
    @Column(name = "u_lmod_id", columnDefinition = "integer DEFAULT 1", nullable = false)
    private int version;

    @Column(name = "money", scale = 2, precision = 12)
    private BigDecimal money;

    @Column(name = "monthly_income", scale = 2, precision = 12)
    private BigDecimal monthlyIncome;

    @Column(name = "currency")
    @Enumerated(EnumType.STRING)
    private CurrencyType currency;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Override
    public Long getId() {
        return id;
    }
}

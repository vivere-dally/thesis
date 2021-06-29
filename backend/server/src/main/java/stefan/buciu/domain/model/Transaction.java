package stefan.buciu.domain.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@javax.persistence.Entity
@javax.persistence.Table(name = "transactions")
public class Transaction implements Entity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Version
    @Column(name = "u_lmod_id", columnDefinition = "integer DEFAULT 1", nullable = false)
    private int version;

    @Column(name = "message")
    private String message;

    @Column(name = "value", scale = 2, precision = 12)
    private BigDecimal value;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private TransactionType type;

    @Column(name = "date")
    private LocalDateTime date;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @Override
    public Long getId() {
        return id;
    }

    public interface PerMonthProjection {
        Integer getMonth();
        BigDecimal getSum();
        TransactionType getType();
    }
}

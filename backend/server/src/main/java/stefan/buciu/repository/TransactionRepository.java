package stefan.buciu.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import stefan.buciu.domain.model.Account;
import stefan.buciu.domain.model.Transaction;
import stefan.buciu.domain.model.TransactionType;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Page<Transaction> findAllByAccountAndMessageStartsWith(Account account, String message, Pageable pageable);

    Page<Transaction> findAllByAccountAndMessageStartsWithAndType(Account account,
                                                                  String message,
                                                                  TransactionType transactionType,
                                                                  Pageable pageable);

    @Query(name = "transactionsPerMonth",
            value = "select month(date) as month, type, sum(value) as sum from transactions where account_id = :accountId and year(current_date()) = year(date) group by month(date), type",
            nativeQuery = true)
    List<Transaction.PerMonthProjection> getAllTransactionsPerMonthByAccountId(@Param("accountId") long accountId);
}

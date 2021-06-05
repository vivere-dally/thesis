package stefan.buciu.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import stefan.buciu.domain.model.Account;
import stefan.buciu.domain.model.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Page<Transaction> findAllByAccount(Account account, Pageable pageable);
}

package stefan.buciu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import stefan.buciu.domain.model.Account;
import stefan.buciu.domain.model.User;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByUserAndId(User user, Long accountId);

    List<Account> findAllByUser(User user);
}

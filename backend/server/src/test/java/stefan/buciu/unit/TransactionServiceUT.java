package stefan.buciu.unit;

import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import stefan.buciu.domain.exception.AccountNotFoundException;
import stefan.buciu.domain.model.*;
import stefan.buciu.domain.model.dto.TransactionDTO;
import stefan.buciu.repository.AccountRepository;
import stefan.buciu.repository.TransactionRepository;
import stefan.buciu.service.TransactionService;
import stefan.buciu.service.TransactionServiceImpl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
public class TransactionServiceUT {

    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private AccountRepository accountRepository;

    private TransactionService transactionService;

    @Before
    public void before() {
        transactionService = new TransactionServiceImpl(transactionRepository, accountRepository);
    }

    @Test
    public void save_success_income() {
        User user = new User();
        Account account = new Account(1L, 1, BigDecimal.ONE, BigDecimal.ONE, CurrencyType.RON, user);
        TransactionDTO expected = new TransactionDTO(2L, "test", BigDecimal.ONE, TransactionType.INCOME, LocalDateTime.now());
        Transaction transaction = expected.toEntity();
        transaction.setAccount(account);
        Account updatedAccount = new Account(1L, 1, BigDecimal.ONE.add(BigDecimal.ONE), BigDecimal.ONE, CurrencyType.RON, user);

        mockAccountRepositoryFindById(account.getId(), account);
        when(transactionRepository.save(transaction)).thenReturn(transaction);
        when(accountRepository.save(updatedAccount)).thenReturn(updatedAccount);

        TransactionDTO actual = transactionService.save(account.getId(), expected);

        verify(accountRepository, times(1)).findById(account.getId());
        verify(transactionRepository, times(1)).save(transaction);
        verify(accountRepository, times(1)).save(updatedAccount);
        assertEquals(expected, actual);
    }

    @Test
    public void save_success_expense() {
        User user = new User();
        Account account = new Account(1L, 1, BigDecimal.ONE, BigDecimal.ONE, CurrencyType.RON, user);
        TransactionDTO expected = new TransactionDTO(2L, "test", BigDecimal.ONE, TransactionType.EXPENSE, LocalDateTime.now());
        Transaction transaction = expected.toEntity();
        transaction.setAccount(account);
        Account updatedAccount = new Account(1L, 1, BigDecimal.ONE.subtract(BigDecimal.ONE), BigDecimal.ONE, CurrencyType.RON, user);

        mockAccountRepositoryFindById(account.getId(), account);
        when(transactionRepository.save(transaction)).thenReturn(transaction);
        when(accountRepository.save(updatedAccount)).thenReturn(updatedAccount);

        TransactionDTO actual = transactionService.save(account.getId(), expected);

        verify(accountRepository, times(1)).findById(account.getId());
        verify(transactionRepository, times(1)).save(transaction);
        verify(accountRepository, times(1)).save(updatedAccount);
        assertEquals(expected, actual);
    }

    @Test
    public void save_accountDoesNotExist() {
        long accountId = 1L;
        mockAccountRepositoryFindById(accountId, null);

        assertThrows(AccountNotFoundException.class, () -> transactionService.save(accountId, new TransactionDTO()));
        verify(accountRepository, times(1)).findById(accountId);
    }

    public void mockAccountRepositoryFindById(long id, Account account) {
        Optional<Account> optionalAccount = Optional.empty();
        if (account != null) {
            optionalAccount = Optional.of(account);
        }

        when(accountRepository.findById(id)).thenReturn(optionalAccount);
    }
}

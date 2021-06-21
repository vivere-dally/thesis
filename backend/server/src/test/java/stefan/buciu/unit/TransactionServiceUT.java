package stefan.buciu.unit;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import stefan.buciu.domain.exception.AccountHasInsufficientFundsException;
import stefan.buciu.domain.exception.AccountNotFoundException;
import stefan.buciu.domain.model.*;
import stefan.buciu.domain.model.dto.TransactionDTO;
import stefan.buciu.repository.AccountRepository;
import stefan.buciu.repository.TransactionRepository;
import stefan.buciu.service.TransactionService;
import stefan.buciu.service.TransactionServiceImpl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    @Test
    public void save_notEnoughFunds() {
        User user = new User();
        Account account = new Account(1L, 1, BigDecimal.ZERO, BigDecimal.ONE, CurrencyType.RON, user);
        TransactionDTO expected = new TransactionDTO(2L, "test", BigDecimal.ONE, TransactionType.EXPENSE, LocalDateTime.now());
        Transaction transaction = expected.toEntity();
        transaction.setAccount(account);

        mockAccountRepositoryFindById(account.getId(), account);

        assertThrows(AccountHasInsufficientFundsException.class, () -> transactionService.save(account.getId(), expected));
        verify(accountRepository, times(1)).findById(account.getId());
    }

    @Test
    public void findAllByAccountId_success() {
        Account account = new Account(1L, 1, BigDecimal.ZERO, BigDecimal.ONE, CurrencyType.RON, new User());
        List<Transaction> data = Arrays.asList(
                new Transaction(1L, 1, "test", BigDecimal.ONE, TransactionType.INCOME, LocalDateTime.now(), account),
                new Transaction(2L, 1, "test", BigDecimal.ONE, TransactionType.INCOME, LocalDateTime.now(), account),
                new Transaction(3L, 1, "test", BigDecimal.ONE, TransactionType.INCOME, LocalDateTime.now(), account));
        List<TransactionDTO> expected = data.stream().map(TransactionDTO::new).collect(Collectors.toList());
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE, Sort.by("date").descending().and(Sort.by("id")));
        Page<Transaction> page = new PageImpl<>(data, pageable, data.size());

        mockAccountRepositoryFindById(account.getId(), account);
        when(transactionRepository.findAllByAccountAndMessageStartsWith(account, "", pageable)).thenReturn(page);

        List<TransactionDTO> actual = transactionService.findAllByAccountId(account.getId(), null, null, null, null);

        verify(accountRepository, times(1)).findById(account.getId());
        verify(transactionRepository, times(1)).findAllByAccountAndMessageStartsWith(account, "", pageable);
        assertEquals(expected, actual);
    }

    @Test
    public void findAllByAccountId_incorrectPaginationArguments() {
        long accountId = 1L;
        Integer page = null, size = 5;

        assertThrows(IllegalArgumentException.class, () -> transactionService.findAllByAccountId(accountId, page, size, null, null));
    }

    @Test
    public void findAllByAccountId_accountDoesNotExist() {
        long accountId = 1L;

        mockAccountRepositoryFindById(accountId, null);

        assertThrows(AccountNotFoundException.class, () -> transactionService.findAllByAccountId(accountId, null, null, null, null));
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

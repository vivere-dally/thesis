package stefan.buciu.unit;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import stefan.buciu.domain.exception.AccountNotFoundException;
import stefan.buciu.domain.exception.UserNotFoundException;
import stefan.buciu.domain.model.Account;
import stefan.buciu.domain.model.CurrencyType;
import stefan.buciu.domain.model.User;
import stefan.buciu.domain.model.dto.AccountDTO;
import stefan.buciu.repository.AccountRepository;
import stefan.buciu.repository.UserRepository;
import stefan.buciu.service.AccountService;
import stefan.buciu.service.AccountServiceImpl;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
public class AccountServiceUT {

    @Mock
    private UserRepository userRepository;
    @Mock
    private AccountRepository accountRepository;

    private AccountService accountService;

    @Before
    public void before() {
        accountService = new AccountServiceImpl(accountRepository, userRepository);
    }

    @Test
    public void save_success() {
        AccountDTO expected = new AccountDTO(1L, BigDecimal.ONE, BigDecimal.ONE, CurrencyType.RON);
        User user = new User(2L, 1, "test", "test");
        Account account = expected.toEntity();
        account.setUser(user);
        mockUserRepositoryFindById(user.getId(), user);
        mockAccountRepositoryFindByUserAndId(user, account.getId(), account);
        when(accountRepository.save(account)).thenReturn(account);

        AccountDTO actual = accountService.save(user.getId(), expected);

        verify(userRepository, times(2)).findById(user.getId());
        verify(accountRepository, times(1)).findByUserAndId(user, account.getId());
        verify(accountRepository, times(1)).save(account);
        assertEquals(expected, actual);
    }

    @Test
    public void save_userDoesNotExist() {
        long userId = 1L;
        mockUserRepositoryFindById(userId, null);

        assertThrows(UserNotFoundException.class, () -> accountService.save(userId, new AccountDTO()));
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    public void update_success() {
        AccountDTO expected = new AccountDTO(1L, BigDecimal.ONE, BigDecimal.ONE, CurrencyType.RON);
        User user = new User(2L, 1, "test", "test");
        Account account = expected.toEntity();
        account.setUser(user);
        when(accountRepository.findById(expected.getId())).thenReturn(Optional.of(account));
        mockUserRepositoryFindById(user.getId(), user);
        mockAccountRepositoryFindByUserAndId(user, account.getId(), account);
        when(accountRepository.save(account)).thenReturn(account);

        AccountDTO actual = accountService.update(user.getId(), expected);

        verify(userRepository, times(2)).findById(user.getId());
        verify(accountRepository, times(1)).findByUserAndId(user, account.getId());
        verify(accountRepository, times(1)).findById(expected.getId());
        verify(accountRepository, times(1)).save(account);
        assertEquals(expected, actual);
    }

    @Test
    public void update_accountDoesNotExist() {
        AccountDTO expected = new AccountDTO(1L, BigDecimal.ONE, BigDecimal.ONE, CurrencyType.RON);
        long userId = 2L;
        when(accountRepository.findById(expected.getId())).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> accountService.update(userId, expected));
        verify(accountRepository, times(1)).findById(expected.getId());
    }

    @Test
    public void delete_success() {
        AccountDTO expected = new AccountDTO(1L, BigDecimal.ONE, BigDecimal.ONE, CurrencyType.RON);
        User user = new User(2L, 1, "test", "test");
        Account account = expected.toEntity();
        account.setUser(user);
        when(accountRepository.findById(expected.getId())).thenReturn(Optional.of(account));
        doNothing().when(accountRepository).deleteById(expected.getId());

        AccountDTO actual = accountService.delete(user.getId(), expected.getId());

        verify(accountRepository, times(1)).findById(expected.getId());
        verify(accountRepository, times(1)).deleteById(expected.getId());
        assertEquals(expected, actual);
    }

    @Test
    public void delete_accountDoesNotExist() {
        long accountId = 1L, userId = 2L;
        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> accountService.delete(userId, accountId));
        verify(accountRepository, times(1)).findById(accountId);
    }

    @Test
    public void delete_userDoesNotExist() {
        AccountDTO expected = new AccountDTO(1L, BigDecimal.ONE, BigDecimal.ONE, CurrencyType.RON);
        Account account = expected.toEntity();
        User user = new User(2L, 1, "test", "test");
        account.setUser(user);
        long userId = 3L;
        when(accountRepository.findById(expected.getId())).thenReturn(Optional.of(account));

        assertThrows(UserNotFoundException.class, () -> accountService.delete(userId, expected.getId()));
        verify(accountRepository, times(1)).findById(expected.getId());
    }

    @Test
    public void findByUserIdAndId_success() {
        long userId = 1L, accountId = 2L;
        User user = new User(userId, 1, "a", "b");
        AccountDTO expected = new AccountDTO(accountId, BigDecimal.ONE, BigDecimal.ONE, CurrencyType.RON);
        Account account = expected.toEntity();
        account.setUser(user);
        mockUserRepositoryFindById(userId, user);
        mockAccountRepositoryFindByUserAndId(user, accountId, account);

        AccountDTO actual = accountService.findByUserIdAndId(userId, accountId);

        verify(userRepository, times(1)).findById(userId);
        verify(accountRepository, times(1)).findByUserAndId(user, accountId);
        assertEquals(expected, actual);
    }

    @Test
    public void findByUserIdAndId_accountDoesNotExist() {
        long userId = 1L, accountId = 2L;
        User user = new User(userId, 1, "a", "b");
        mockUserRepositoryFindById(userId, user);
        mockAccountRepositoryFindByUserAndId(user, accountId, null);

        assertThrows(AccountNotFoundException.class, () -> accountService.findByUserIdAndId(userId, accountId));

        verify(userRepository, times(1)).findById(userId);
        verify(accountRepository, times(1)).findByUserAndId(user, accountId);
    }

    @Test
    public void findByUserIdAndId_userDoesNotExist() {
        long userId = 1L, accountId = 2L;
        mockUserRepositoryFindById(userId, null);

        assertThrows(UserNotFoundException.class, () -> accountService.findByUserIdAndId(userId, accountId));

        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    public void findAllByUserId_success() {
        User user = new User(123L, 1, "a", "b");
        List<Account> data = Arrays.asList(
                new Account(1L, 1, BigDecimal.ONE, BigDecimal.ONE, CurrencyType.RON, user),
                new Account(2L, 1, BigDecimal.ONE, BigDecimal.ONE, CurrencyType.RON, user),
                new Account(3L, 1, BigDecimal.ONE, BigDecimal.ONE, CurrencyType.RON, user));
        List<AccountDTO> expected = data.stream().map(AccountDTO::new).collect(Collectors.toList());
        mockUserRepositoryFindById(user.getId(), user);
        when(accountRepository.findAllByUser(user)).thenReturn(data);

        List<AccountDTO> actual = accountService.findAllByUserId(user.getId());

        verify(userRepository, times(1)).findById(user.getId());
        verify(accountRepository, times(1)).findAllByUser(user);
        assertEquals(expected, actual);
    }

    @Test
    public void findAllByUserId_userDoesNotExist() {
        long userId = 1L;
        mockUserRepositoryFindById(userId, null);

        assertThrows(UserNotFoundException.class, () -> accountService.findAllByUserId(userId));

        verify(userRepository, times(1)).findById(userId);
    }

    private void mockUserRepositoryFindById(long id, User user) {
        Optional<User> optionalUser = Optional.empty();
        if (user != null) {
            optionalUser = Optional.of(user);
        }

        when(userRepository.findById(id)).thenReturn(optionalUser);
    }

    private void mockAccountRepositoryFindByUserAndId(User user, long id, Account account) {
        Optional<Account> optionalAccount = Optional.empty();
        if (account != null) {
            optionalAccount = Optional.of(account);
        }

        when(accountRepository.findByUserAndId(user, id)).thenReturn(optionalAccount);
    }
}

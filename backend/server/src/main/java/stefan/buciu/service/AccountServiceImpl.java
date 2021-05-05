package stefan.buciu.service;

import org.springframework.stereotype.Service;
import stefan.buciu.domain.exception.AccountNotFoundException;
import stefan.buciu.domain.exception.UserNotFoundException;
import stefan.buciu.domain.model.Account;
import stefan.buciu.domain.model.User;
import stefan.buciu.domain.model.dto.AccountDTO;
import stefan.buciu.repository.AccountRepository;
import stefan.buciu.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    public AccountServiceImpl(AccountRepository accountRepository, UserRepository userRepository) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
    }

    @Override
    public AccountDTO save(long userId, AccountDTO accountDTO) {
        Account account = accountDTO.toEntity();
        account.setUser(findUserByIdOrThrow(userId));

        account = this.accountRepository.save(account);
        return this.findByUserIdAndId(userId, account.getId());
    }

    @Override
    public AccountDTO update(long userId, AccountDTO accountDTO) {
        if (this.accountRepository.findById(accountDTO.getId()).isEmpty()) {
            throw new AccountNotFoundException();
        }

        return this.save(userId, accountDTO);
    }

    @Override
    public AccountDTO delete(long userId, long accountId) {
        Account account = this.accountRepository
                .findById(accountId)
                .orElseThrow(AccountNotFoundException::new);

        if (account.getUser().getId() != userId) {
            throw new UserNotFoundException();
        }

        this.accountRepository.deleteById(accountId);
        return new AccountDTO(account);
    }

    @Override
    public AccountDTO findByUserIdAndId(long userId, long accountId) {
        return new AccountDTO(this.accountRepository
                .findByUserAndId(this.findUserByIdOrThrow(userId), accountId)
                .orElseThrow(AccountNotFoundException::new));
    }

    @Override
    public List<AccountDTO> findAllByUserId(long userId) {
        return this.accountRepository.findAllByUser(findUserByIdOrThrow(userId))
                .stream()
                .map(AccountDTO::new)
                .collect(Collectors.toList());
    }

    private User findUserByIdOrThrow(long userId) {
        return this.userRepository
                .findById(userId)
                .orElseThrow(UserNotFoundException::new);
    }
}

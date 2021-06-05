package stefan.buciu.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stefan.buciu.domain.exception.AccountHasInsufficientFundsException;
import stefan.buciu.domain.exception.AccountNotFoundException;
import stefan.buciu.domain.model.Account;
import stefan.buciu.domain.model.Transaction;
import stefan.buciu.domain.model.TransactionType;
import stefan.buciu.domain.model.dto.TransactionDTO;
import stefan.buciu.repository.AccountRepository;
import stefan.buciu.repository.TransactionRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    public TransactionServiceImpl(TransactionRepository transactionRepository, AccountRepository accountRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
    }

    @Transactional
    @Override
    public TransactionDTO save(long accountId, TransactionDTO transactionDTO) {
        Account account = this.findAccountByIdOrThrow(accountId);
        Transaction transaction = transactionDTO.toEntity();
        if (transaction.getType() == TransactionType.EXPENSE &&
                account.getMoney().subtract(transaction.getValue()).compareTo(BigDecimal.ZERO) == -1) {
            throw new AccountHasInsufficientFundsException();
        }

        transaction.setAccount(account);
        transaction = this.transactionRepository.save(transaction);
        switch (transaction.getType()) {
            case INCOME:
                account.setMoney(account.getMoney().add(transaction.getValue()));
                break;
            case EXPENSE:
                account.setMoney(account.getMoney().subtract(transaction.getValue()));
                break;
        }

        this.accountRepository.save(account);
        return new TransactionDTO(transaction);
    }

    @Override
    public List<TransactionDTO> findAllByAccountId(long accountId, Integer page, Integer size) {
        Optional<Integer> optionalPage = Optional.ofNullable(page);
        Optional<Integer> optionalSize = Optional.ofNullable(size);
        if (optionalPage.isEmpty() && optionalSize.isPresent()) {
            throw new IllegalArgumentException("While page is null, size cannot have a value");
        }
        else if (optionalPage.isPresent() && optionalSize.isEmpty()) {
            optionalSize = Optional.of(5);
        }

        Account account = this.findAccountByIdOrThrow(accountId);
        Pageable pageable = PageRequest.of(
                optionalPage.orElse(0),
                optionalSize.orElse(Integer.MAX_VALUE),
                Sort.by("date").descending().and(Sort.by("id"))
        );
        return this.transactionRepository
                .findAllByAccount(account, pageable)
                .getContent()
                .stream()
                .map(TransactionDTO::new)
                .collect(Collectors.toList());
    }

    private Account findAccountByIdOrThrow(long accountId) {
        return this.accountRepository
                .findById(accountId)
                .orElseThrow(AccountNotFoundException::new);
    }
}

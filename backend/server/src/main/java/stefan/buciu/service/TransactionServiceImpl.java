package stefan.buciu.service;

import org.springframework.data.domain.Page;
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
import stefan.buciu.domain.model.dto.TransactionSumsPerMonthDTO;
import stefan.buciu.repository.AccountRepository;
import stefan.buciu.repository.TransactionRepository;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.IntFunction;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

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
                account.getMoney().subtract(transaction.getValue()).compareTo(BigDecimal.ZERO) < 0) {
            throw new AccountHasInsufficientFundsException();
        }

        transaction.setAccount(account);
        transaction = this.transactionRepository.save(transaction);
        if (transaction.getType() == TransactionType.INCOME) {
            account.setMoney(account.getMoney().add(transaction.getValue()));
        }
        else if (transaction.getType() == TransactionType.EXPENSE) {
            account.setMoney(account.getMoney().subtract(transaction.getValue()));
        }

        this.accountRepository.save(account);
        return new TransactionDTO(transaction);
    }

    @Override
    public List<TransactionDTO> findAllByAccountId(long accountId, Integer page, Integer size, String message, TransactionType transactionType) {
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

        message = (message == null) ? "" : message;
        Page<Transaction> result = (transactionType == null) ?
                this.transactionRepository.findAllByAccountAndMessageStartsWith(account, message, pageable) :
                this.transactionRepository.findAllByAccountAndMessageStartsWithAndType(account, message, transactionType, pageable);

        return result.getContent()
                .stream()
                .map(TransactionDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<TransactionSumsPerMonthDTO> getAllTransactionValuesPerMonthByAccountId(long accountId) {
        Account account = this.findAccountByIdOrThrow(accountId);
        return this.transactionRepository
                .getAllTransactionsPerMonthByAccountId(account.getId())
                .stream()
                .collect(groupingBy(Transaction.PerMonthProjection::getMonth))
                .entrySet()
                .stream()
                .map(entry -> {
                    IntFunction<TransactionSumsPerMonthDTO> lambda = (int index) -> {
                        if (entry.getValue().get(index).getType() == TransactionType.INCOME) {
                            return new TransactionSumsPerMonthDTO(entry.getKey(), entry.getValue().get(index).getSum(), BigDecimal.ZERO);
                        }

                        return new TransactionSumsPerMonthDTO(entry.getKey(), BigDecimal.ZERO, entry.getValue().get(index).getSum());
                    };

                    var first = lambda.apply(0);
                    if (entry.getValue().size() == 2) {
                        var second = lambda.apply(1);
                        first.setIncome(first.getIncome().add(second.getIncome()));
                        first.setExpense(first.getExpense().add(second.getExpense()));
                    }

                    return first;
                })
                .collect(Collectors.toList());
    }

    private Account findAccountByIdOrThrow(long accountId) {
        return this.accountRepository
                .findById(accountId)
                .orElseThrow(AccountNotFoundException::new);
    }
}

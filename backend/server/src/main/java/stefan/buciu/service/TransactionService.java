package stefan.buciu.service;

import stefan.buciu.domain.model.TransactionType;
import stefan.buciu.domain.model.dto.TransactionDTO;
import stefan.buciu.domain.model.dto.TransactionSumsPerMonthDTO;

import java.util.List;

public interface TransactionService {

    TransactionDTO save(long accountId, TransactionDTO transactionDTO);

    List<TransactionDTO> findAllByAccountId(long accountId, Integer page, Integer size, String message, TransactionType transactionType);

    List<TransactionSumsPerMonthDTO> getAllTransactionValuesPerMonthByAccountId(long accountId);
}

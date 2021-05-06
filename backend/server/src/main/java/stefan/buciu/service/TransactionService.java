package stefan.buciu.service;

import stefan.buciu.domain.model.dto.TransactionDTO;

import java.util.List;

public interface TransactionService {

    TransactionDTO save(long accountId, TransactionDTO transactionDTO);

    List<TransactionDTO> findAllByAccountId(long accountId, Integer page, Integer size);
}

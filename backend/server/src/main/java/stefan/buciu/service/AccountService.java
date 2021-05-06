package stefan.buciu.service;

import stefan.buciu.domain.model.dto.AccountDTO;

import java.util.List;

public interface AccountService {

    AccountDTO save(long userId, AccountDTO accountDTO);

    AccountDTO update(long userId, AccountDTO accountDTO);

    AccountDTO delete(long userId, long accountId);

    AccountDTO findByUserIdAndId(long userId, long accountId);

    List<AccountDTO> findAllByUserId(long userId);
}

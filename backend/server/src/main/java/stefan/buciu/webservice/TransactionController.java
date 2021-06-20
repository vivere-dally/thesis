package stefan.buciu.webservice;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import stefan.buciu.domain.exception.EntitySocketNotificationException;
import stefan.buciu.domain.model.dto.AccountDTO;
import stefan.buciu.domain.model.dto.TransactionDTO;
import stefan.buciu.domain.model.dto.TransactionSumsPerMonthDTO;
import stefan.buciu.domain.model.notification.Action;
import stefan.buciu.service.AccountService;
import stefan.buciu.service.TransactionService;
import stefan.buciu.webnotification.EntitySocketHandler;

import java.util.List;

@Api(value = "/user/{userId}/account/{accountId}/transaction", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@PreAuthorize("isAuthenticated()")
@RequestMapping("/user/{userId}/account/{accountId}/transaction")
@RestController
public class TransactionController {

    private final AccountService accountService;
    private final TransactionService transactionService;

    private final EntitySocketHandler entitySocketHandler;

    public TransactionController(AccountService accountService, TransactionService transactionService, EntitySocketHandler entitySocketHandler) {
        this.accountService = accountService;
        this.transactionService = transactionService;

        this.entitySocketHandler = entitySocketHandler;
    }

    @GetMapping()
    public ResponseEntity<List<TransactionDTO>> get(
            @ApiParam(name = "accountId", type = "long", value = "ID of the Account", example = "1")
            @PathVariable Long accountId,

            // Pagination
            @ApiParam(name = "page", type = "Integer", value = "Number of the page", example = "2")
            @RequestParam(required = false) Integer page,
            @ApiParam(name = "size", type = "Integer", value = "The size of one page", example = "5")
            @RequestParam(required = false) Integer size
    ) {
        log.debug("Entered class = TransactionController & method = get");

        List<TransactionDTO> result = this.transactionService.findAllByAccountId(accountId, page, size);
        return ResponseEntity
                .ok(result);
    }

    @PostMapping()
    public ResponseEntity<TransactionDTO> save(
            @ApiParam(name = "userId", type = "long", value = "ID of the User", example = "1")
            @PathVariable Long userId,
            @ApiParam(name = "accountId", type = "long", value = "ID of the Account", example = "1")
            @PathVariable Long accountId,

            @ApiParam(name = "transactionDTO", type = "TransactionDTO")
            @RequestBody TransactionDTO transactionDTO
    ) throws EntitySocketNotificationException {
        log.debug("Entered class = TransactionController & method = save");

        TransactionDTO result = this.transactionService.save(accountId, transactionDTO);
        AccountDTO account = this.accountService.findByUserIdAndId(userId, accountId);
        this.entitySocketHandler.notifySessions(account, Action.PUT, userId);

        return ResponseEntity
                .ok(result);
    }

    @GetMapping("/report/sumsPerMonth")
    public ResponseEntity<List<TransactionSumsPerMonthDTO>> getReport(
            @ApiParam(name = "accountId", type = "long", value = "ID of the Account", example = "1")
            @PathVariable Long accountId
    ) {
        log.debug("Entered class = TransactionController & method = getReport");

        var result = this.transactionService.getAllTransactionValuesPerMonthByAccountId(accountId);
        return ResponseEntity
                .ok(result);
    }
}


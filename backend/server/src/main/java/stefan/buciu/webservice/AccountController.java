package stefan.buciu.webservice;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import stefan.buciu.domain.model.dto.AccountDTO;
import stefan.buciu.service.AccountService;

import java.util.List;

@Api(value = "/user/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@PreAuthorize("isAuthenticated()")
@RequestMapping("/user/{userId}")
@RestController
public class AccountController {
    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/account")
    public ResponseEntity<List<AccountDTO>> get(
            @ApiParam(name = "userId", type = "long", value = "ID of the User", example = "1")
            @PathVariable Long userId
    ) {
        log.debug("Entered class = AccountController & method = get");

        List<AccountDTO> result = this.accountService.findAllByUserId(userId);
        return ResponseEntity
                .ok(result);
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<AccountDTO> getById(
            @ApiParam(name = "userId", type = "long", value = "ID of the User", example = "1")
            @PathVariable Long userId,

            @ApiParam(name = "accountId", type = "long", value = "ID of the Account", example = "1")
            @PathVariable Long accountId
    ) {
        log.debug("Entered class = AccountController & method = getById");

        AccountDTO result = this.accountService.findByUserIdAndId(userId, accountId);
        return ResponseEntity
                .ok(result);
    }

    @PostMapping("/account")
    public ResponseEntity<AccountDTO> save(
            @ApiParam(name = "userId", type = "long", value = "ID of the User", example = "1")
            @PathVariable Long userId,

            @ApiParam(name = "accountDTO", type = "AccountDTO")
            @RequestBody AccountDTO accountDTO
    ) {
        log.debug("Entered class = AccountController & method = save");

        AccountDTO result = this.accountService.save(userId, accountDTO);
        return ResponseEntity
                .ok(result);
    }

    @PutMapping("/account/{accountId}")
    public ResponseEntity<AccountDTO> update(
            @ApiParam(name = "userId", type = "long", value = "ID of the User", example = "1")
            @PathVariable Long userId,

            @ApiParam(name = "accountId", type = "long", value = "ID of the Account", example = "1")
            @PathVariable Long accountId,

            @ApiParam(name = "accountDTO", type = "AccountDTO")
            @RequestBody AccountDTO accountDTO
    ) {
        log.debug("Entered class = AccountController & method = update");

        accountDTO.setId(accountId);
        AccountDTO result = this.accountService.update(userId, accountDTO);
        return ResponseEntity
                .ok(result);
    }

    @DeleteMapping("/account/{accountId}")
    public ResponseEntity<AccountDTO> delete(
            @ApiParam(name = "userId", type = "long", value = "ID of the User", example = "1")
            @PathVariable Long userId,

            @ApiParam(name = "accountId", type = "long", value = "ID of the Account", example = "1")
            @PathVariable Long accountId
    ) {
        log.debug("Entered class = AccountController & method = delete");

        AccountDTO result = this.accountService.delete(userId, accountId);
        return ResponseEntity
                .ok(result);
    }
}

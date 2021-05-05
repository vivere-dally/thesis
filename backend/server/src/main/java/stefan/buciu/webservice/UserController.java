package stefan.buciu.webservice;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import stefan.buciu.domain.model.dto.UserAuthenticatedDTO;
import stefan.buciu.domain.model.dto.UserLoginDTO;
import stefan.buciu.domain.model.dto.UserSignupDTO;
import stefan.buciu.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Slf4j
@RestController
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "User created successfully."),
            @ApiResponse(code = 409, message = "Username already exists."),
            @ApiResponse(code = 500, message = "System error.")
    })
    @ApiOperation(value = "creates a new user", response = String.class, produces = MediaType.TEXT_HTML_VALUE)
    @PostMapping("/signup")
    public ResponseEntity<String> signup(
            @ApiParam(name = "userSignupDTO", value = "User signup credentials.")
            @Valid @RequestBody UserSignupDTO userSignupDTO
    ) {
        userSignupDTO.setPassword(this.passwordEncoder.encode(userSignupDTO.getPassword()));
        this.userService.signupUser(userSignupDTO);
        return ResponseEntity.ok("User created successfully.");
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "User authenticated successfully."),
            @ApiResponse(code = 403, message = "Bad credentials."),
            @ApiResponse(code = 500, message = "System error.")
    })
    @ApiOperation(value = "authenticate a user", response = UserAuthenticatedDTO.class, produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping("/login")
    public ResponseEntity<UserAuthenticatedDTO> login(
            @ApiParam(name = "userLoginDTO", value = "User login credentials.") UserLoginDTO userLoginDTO,
            HttpServletRequest request
    ) {
        String username = (String) request.getAttribute("username");
        UserAuthenticatedDTO userAuthenticatedDTO = this.userService.findByUsername(username);
        return ResponseEntity.ok(userAuthenticatedDTO);
    }
}

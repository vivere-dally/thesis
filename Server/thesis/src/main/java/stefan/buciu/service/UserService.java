package stefan.buciu.service;

import stefan.buciu.domain.model.dto.UserAuthenticatedDTO;
import stefan.buciu.domain.model.dto.UserSignupDTO;

import java.util.Optional;

public interface UserService {

    void signupUser(UserSignupDTO userSignup);

    Optional<UserAuthenticatedDTO> findByUsername(String username);
}

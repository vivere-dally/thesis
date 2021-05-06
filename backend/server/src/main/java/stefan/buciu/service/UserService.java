package stefan.buciu.service;

import stefan.buciu.domain.model.dto.UserAuthenticatedDTO;
import stefan.buciu.domain.model.dto.UserSignupDTO;

public interface UserService {

    void signupUser(UserSignupDTO userSignup);

    UserAuthenticatedDTO findByUsername(String username);
}

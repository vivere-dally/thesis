package stefan.buciu.service;

import org.springframework.stereotype.Service;
import stefan.buciu.domain.exception.UserAlreadyExistsException;
import stefan.buciu.domain.exception.UserNotFoundException;
import stefan.buciu.domain.model.User;
import stefan.buciu.domain.model.dto.UserAuthenticatedDTO;
import stefan.buciu.domain.model.dto.UserSignupDTO;
import stefan.buciu.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void signupUser(UserSignupDTO userSignup) {
        User user = userSignup.toEntity();
        // TODO - do I need authorities for the user?
        if (this.userRepository.existsByUsername(user.getUsername())) {
            throw new UserAlreadyExistsException();
        }

        this.userRepository.save(user);
    }

    @Override
    public UserAuthenticatedDTO findByUsername(String username) {
        return new UserAuthenticatedDTO(this.userRepository
                .findByUsername(username)
                .orElseThrow(UserNotFoundException::new));
    }
}

package stefan.buciu.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import stefan.buciu.domain.exception.UserAlreadyExistsException;
import stefan.buciu.domain.model.User;
import stefan.buciu.domain.model.dto.UserAuthenticatedDTO;
import stefan.buciu.domain.model.dto.UserSignupDTO;
import stefan.buciu.repository.UserRepository;

import java.util.Optional;

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
            throw new UserAlreadyExistsException("A user with the given username already exists.");
        }

        try {
            this.userRepository.save(user);
        } catch (Exception exception) {
            System.out.println("???");
        }
    }

    @Override
    public Optional<UserAuthenticatedDTO> findByUsername(String username) {
        return this.userRepository.findByUsername(username)
                .map(UserAuthenticatedDTO::new)
                .or(Optional::empty);
    }
}

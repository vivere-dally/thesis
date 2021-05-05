package stefan.buciu.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import stefan.buciu.domain.exception.UserNotFoundException;
import stefan.buciu.domain.model.SecurityUser;
import stefan.buciu.domain.model.User;
import stefan.buciu.repository.UserRepository;

import java.util.ArrayList;

@Service
public class SecurityUserServiceImpl implements SecurityUserService {

    private final UserRepository userRepository;

    public SecurityUserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = this.userRepository.findByUsername(username)
                .orElseThrow(UserNotFoundException::new);

        // TODO - do I need authorities for the user?
        return new SecurityUser(username, user.getPassword(), new ArrayList<>());
    }
}

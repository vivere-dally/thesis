package stefan.buciu.unit;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import stefan.buciu.domain.exception.UserAlreadyExistsException;
import stefan.buciu.domain.exception.UserNotFoundException;
import stefan.buciu.domain.model.User;
import stefan.buciu.domain.model.dto.UserAuthenticatedDTO;
import stefan.buciu.domain.model.dto.UserSignupDTO;
import stefan.buciu.repository.UserRepository;
import stefan.buciu.service.UserService;
import stefan.buciu.service.UserServiceImpl;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
public class UserServiceUT {

    @Mock
    private UserRepository userRepository;

    private UserService userService;

    @Before
    public void before() {
        userService = new UserServiceImpl(userRepository);
    }

    @Test
    public void signupUser_success() {
        UserSignupDTO userSignupDTO = new UserSignupDTO("test", "test");
        User user = userSignupDTO.toEntity();
        when(userRepository.existsByUsername(user.getUsername())).thenReturn(false);
        when(userRepository.save(user)).thenReturn(user);

        userService.signupUser(userSignupDTO);

        verify(userRepository, times(1)).existsByUsername(user.getUsername());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void signupUser_exists() {
        UserSignupDTO userSignupDTO = new UserSignupDTO("test", "test");
        User user = userSignupDTO.toEntity();
        when(userRepository.existsByUsername(user.getUsername())).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> userService.signupUser(userSignupDTO));
        verify(userRepository, times(1)).existsByUsername(user.getUsername());
    }

    @Test
    public void findByUsername_exists() {
        User user = new User(1L, 1, "test", "test");
        UserAuthenticatedDTO expected = new UserAuthenticatedDTO(user);
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));

        UserAuthenticatedDTO actual = userService.findByUsername(user.getUsername());

        verify(userRepository, times(1)).findByUsername(user.getUsername());
        assertEquals(expected, actual);
    }

    @Test
    public void findByUsername_doesNotExist() {
        String username = "test";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.findByUsername(username));
        verify(userRepository, times(1)).findByUsername(username);
    }
}

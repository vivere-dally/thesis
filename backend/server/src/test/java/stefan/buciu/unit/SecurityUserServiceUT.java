package stefan.buciu.unit;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import stefan.buciu.domain.exception.UserNotFoundException;
import stefan.buciu.domain.model.SecurityUser;
import stefan.buciu.domain.model.User;
import stefan.buciu.repository.UserRepository;
import stefan.buciu.service.SecurityUserService;
import stefan.buciu.service.SecurityUserServiceImpl;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
public class SecurityUserServiceUT {

    @Mock
    private UserRepository userRepository;

    private SecurityUserService securityUserService;

    @Before
    public void before() {
        securityUserService = new SecurityUserServiceImpl(userRepository);
    }

    @Test
    public void loadUserByUsername_exists() {
        User user = new User(1L, 1, "test", "test");
        UserDetails expected = new SecurityUser(user.getUsername(), user.getPassword(), new ArrayList<>());
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));

        UserDetails actual = securityUserService.loadUserByUsername(user.getUsername());

        verify(userRepository, times(1)).findByUsername(user.getUsername());
        assertEquals(expected, actual);
    }

    @Test
    public void loadUserByUsername_doesNotExist() {
        String username = "test";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> securityUserService.loadUserByUsername(username));
        verify(userRepository, times(1)).findByUsername(username);
    }
}

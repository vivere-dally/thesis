package stefan.buciu.domain.model;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class SecurityUser extends org.springframework.security.core.userdetails.User {

    public SecurityUser(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
    }

    public User toAppUser() {
        return new User(0L, 0, getUsername(), getPassword());
    }
}

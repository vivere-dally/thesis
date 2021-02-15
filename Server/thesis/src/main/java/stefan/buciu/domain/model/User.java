package stefan.buciu.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@javax.persistence.Entity
@javax.persistence.Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(name = "unique_username", columnNames = {"username"})
})
public class User implements Entity<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Version
    @Column(name = "u_lmod_id", columnDefinition = "integer DEFAULT 1", nullable = false)
    private int version;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Override
    public Long getId() {
        return id;
    }
}

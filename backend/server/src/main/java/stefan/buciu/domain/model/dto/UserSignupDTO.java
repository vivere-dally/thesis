package stefan.buciu.domain.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import stefan.buciu.domain.model.User;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSignupDTO implements DTO<User, Long> {

    @ApiModelProperty(required = true, example = "John Doe", value = "username")
    @Size(max = 255, message = "Username cannot be longer than 255 characters.")
    @NotNull(message = "Username cannot be null.")
    private String username;

    @ApiModelProperty(required = true, example = "aPassword", value = "password")
    @Size(max = 255, message = "Password cannot be longer than 255 characters.")
    @NotNull(message = "Password cannot be null.")
    private String password;

    @Override
    public User toEntity() {
        User user = new User();
        user.setUsername(this.username);
        user.setPassword(this.password);
        return user;
    }
}

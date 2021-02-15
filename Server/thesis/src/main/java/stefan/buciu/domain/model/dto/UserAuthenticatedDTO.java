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
public class UserAuthenticatedDTO implements DTO<User, Long> {

    @ApiModelProperty(example = "1", value = "id")
    private long id;

    @ApiModelProperty(required = true, example = "John Doe", value = "username")
    @Size(max = 255, message = "Username cannot be longer than 255 characters.")
    @NotNull(message = "Username cannot be null.")
    private String username;

    public UserAuthenticatedDTO(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
    }

    @Override
    public User toEntity() {
        return null;
    }
}

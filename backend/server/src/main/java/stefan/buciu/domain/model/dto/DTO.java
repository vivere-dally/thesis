package stefan.buciu.domain.model.dto;

import stefan.buciu.domain.model.Entity;

import java.io.Serializable;

public interface DTO<E extends Entity<T>, T extends Serializable> extends Serializable {
    E toEntity();
}

package stefan.buciu.domain.model;

import java.io.Serializable;

public interface Entity<ID extends Serializable> extends Serializable {
    ID getId();
}

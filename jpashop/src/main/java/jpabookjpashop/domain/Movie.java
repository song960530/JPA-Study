package jpabookjpashop.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;

@Entity
@Data
@NoArgsConstructor
public class Movie extends Item {
    private String director;
    private String actor;
}

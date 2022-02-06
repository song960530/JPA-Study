package jpabookjpashop.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("M")
@Data
@NoArgsConstructor
public class Movie extends Item {
    private String director;
    private String actor;
}

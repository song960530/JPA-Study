package jpabookjpashop.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;

@Entity
@Data
@NoArgsConstructor
public class Album extends Item {
    private String artist;
    private String etc;
}

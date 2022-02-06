package jpabookjpashop.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;

@Entity
@Data
@NoArgsConstructor
public class Book extends Item {
    private String author;
    private String isbn;
}

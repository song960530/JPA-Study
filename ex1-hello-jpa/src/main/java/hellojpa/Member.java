package hellojpa;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
// @Table(name="MEMBER")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Member {
    @Id
    private Long id;

    //@Column(name = "name")
    private String name;

}

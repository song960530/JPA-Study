package hellojpa;

import lombok.*;

import javax.persistence.*;
import java.util.Date;

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

    @Column(name = "name")
    private String username;

    private Integer age;

    @Enumerated(EnumType.STRING) // Enum의 경우 꼭 String으로 설정해줘야함.
    private RoleType roleType;

    @Temporal(TemporalType.TIMESTAMP)
    private Date regDt;

    @Temporal(TemporalType.TIMESTAMP)
    private Date modiDt;

    @Lob
    private String description;
}
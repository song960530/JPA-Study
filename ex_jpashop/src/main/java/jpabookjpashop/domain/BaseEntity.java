package jpabookjpashop.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@MappedSuperclass
@Data
@NoArgsConstructor
public abstract class BaseEntity {
    @Column(name = "REG_ID")
    private String regId;
    @Column(name = "REG_DT")
    private LocalDateTime regDt;
    @Column(name = "MODI_ID")
    private String modiId;
    @Column(name = "MODI_DT")
    private LocalDateTime modiDt;
}

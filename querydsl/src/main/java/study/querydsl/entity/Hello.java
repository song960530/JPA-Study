package study.querydsl.entity;

import lombok.Getter;
import org.springframework.web.bind.annotation.GetMapping;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
public class Hello {

    @Id
    @GeneratedValue
    private Long id;
}

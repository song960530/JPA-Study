package jpabookjpashop.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import javax.persistence.Embeddable;

@Embeddable
@Getter
@EqualsAndHashCode
public class Address {
    private String city;
    private String zipcode;
    private String street;
}

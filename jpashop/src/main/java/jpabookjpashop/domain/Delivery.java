package jpabookjpashop.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Delivery {

    @Id
    @GeneratedValue
    @Column(name = "DELIVERY_ID")
    private Long id;

    private String city;
    private String street;
    private String zipcode;

    @OneToOne(mappedBy = "delivery")
    private Order order;

    private DeliveryStatus status;
}

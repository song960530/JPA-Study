package jpabookjpashop.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ORDERS")
public class Order {
    @Id
    @GeneratedValue
    @Column(name = "ORDER_ID")
    private Long id;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private LocalDateTime orderDate;

    @ManyToOne
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    @OneToOne
    @JoinColumn(name = "DELIVERY_ID")
    private Delivery delivery;

    @OneToMany(mappedBy = "order")
    private List<OrderItem> orderItems = new ArrayList<>();
}

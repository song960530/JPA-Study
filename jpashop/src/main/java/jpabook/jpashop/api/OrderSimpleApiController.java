package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderSearch;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.SimpleOrderQueryDto;
import jpabook.jpashop.repository.order.OrderRepository;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
public class OrderSimpleApiController {
    private final OrderRepository orderRepository;
    private final OrderSimpleQueryRepository orderSimpleQueryRepository;

    // 절대 하면 안되는 엔티티 반환
    @GetMapping("/api/v1/simple-orders")
    public List<Order> orderV1() {
        List<Order> all = orderRepository.findAllByString(new OrderSearch());

        for (Order order : all) {
            order.getMember().getName(); // Lazy 강제 초기화
            order.getDelivery().getAddress(); // Lazy 강제 초기화
        }

        return all;
    }

    // Lazy Loading(지연로딩)을 사용하는 방법 (mapper에선 Order엔티티로 조회)
    @GetMapping("/api/v2/simple-orders")
    public List<SimpleOrderDto> ordersV2() {
        return orderRepository.findAllByString(new OrderSearch()).stream().map(SimpleOrderDto::new).collect(Collectors.toList());
    }

    // Lazy 페치 조인을 사용하는 방법 (mapper에선 Order엔티티로 조회)
    @GetMapping("/api/v3/simple-orders")
    public List<SimpleOrderDto> ordersV3() {
        List<Order> orders = orderRepository.findAllWithMemberDelivery();
        return orders.stream().map(o -> new SimpleOrderDto(o)).collect(Collectors.toList());
    }

    // v3와 비슷비슷한 성능을 갖고 있음. dto를 사용하여 조회.
    @GetMapping("/api/v4/simple-orders")
    public List<SimpleOrderQueryDto> orderV4() {
        return orderSimpleQueryRepository.findOrderDtos();
    }


    @Data
    static class SimpleOrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;

        public SimpleOrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName();
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress();
        }
    }

}

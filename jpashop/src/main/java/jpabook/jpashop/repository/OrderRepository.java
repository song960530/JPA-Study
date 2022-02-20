package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor // Spring Data Jpa를 사용하면 PersistenceContext도 생성자를 통해 자동으로 주입해준다
public class OrderRepository {
    private final EntityManager em;

    public void save(Order order) {
        em.persist(order);
    }

    public Order findOne(Long id) {
        return em.find(Order.class, id);
    }

    public List<Order> findAll(OrderSearch orderSearch) {
        // JPQL, Criteria를 사용하여 동적쿼리 생성이 가능하지만 유지보수성이 급격하게 떨어지므로 동적쿼리에선 쿼리QSL을 권장
        // 여기서도 따로 적진않고 쿼리QSL로 작성하겠음.
        return null;
    }

}

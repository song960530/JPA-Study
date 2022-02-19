package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
@RequiredArgsConstructor // Spring Data Jpa를 사용하면 PersistenceContext도 생성자를 통해 자동으로 주입해준다
public class MemberRepository {

    // @PersistenceContext 어노테이션을 붙이면 자동으로 엔티티매니저를 설정 및 생성해준다
    // @PersistenceContext
    private final EntityManager em;

    public void save(Member member) {
        em.persist(member);
    }

    public Member findOne(Long id) {
        return em.find(Member.class, id);
    }

    public List<Member> findAll() {
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }

    public List<Member> findByNamer(String name) {
        return em.createQuery("select m from Member m where 1=1 and m.name = :name", Member.class)
                .setParameter("name", name)
                .getResultList();
    }


}

package hellojpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class JpaMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
//            Member member = Member.builder().id(3L).name("HelloA").build();
//            em.persist(member);
//            member.setName("HelloCCC");
//            Member member2 = em.find(Member.class,3L);
//            System.out.println(member2.getId());

            Member findMember = em.find(Member.class, 1L);

            // JPQL 사용
            // SQL을 추상화한 객체지향SQL
            // 테이블이 아닌 객체를 대상으로 검색하는 객체지향쿼리
            List<Member> resultList = em.createQuery("select m from Member as m", Member.class).setFirstResult(0).setMaxResults(5).getResultList();

            System.out.println(findMember.getName());
            for (Member member : resultList) {
                System.out.println("Member.name = " + member.getName());
            }
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
            emf.close();
        }
    }
}

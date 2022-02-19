package jpabook.jpashop;

import jpabook.jpashop.domain.Member;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class MemberRepositoryTest {
    @Autowired
    MemberRepository memberRepository;

    @Test
    @Transactional
    @Rollback(false) // DB에 값 들어갔는지 보고싶을 때
    public void testMember() throws Exception {
        // given
        Member member = new Member();
        member.setName("memberA");

        // when
        Long saveId = memberRepository.save(member);
        Member findMember = memberRepository.find(member.getId());

        // then
        Assertions.assertEquals(findMember.getId(), member.getId());
        Assertions.assertEquals(findMember.getName(), member.getName());
        Assertions.assertEquals(findMember, member);
    }


    @Test
    public void ExceptionTest() {
        try {
            List<Integer> aa = new ArrayList<>();
            aa.get(3);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("----------------------------------------------");
            System.out.println("----------------------------------------------");
            System.out.println("----------------------------------------------");
            System.out.println(e);
            System.out.println("----------------------------------------------");
            System.out.println("----------------------------------------------");
            System.out.println("----------------------------------------------");
            System.out.println(e.getMessage());
            System.out.println("----------------------------------------------");
            System.out.println("----------------------------------------------");
            System.out.println("----------------------------------------------");
        }

    }

}
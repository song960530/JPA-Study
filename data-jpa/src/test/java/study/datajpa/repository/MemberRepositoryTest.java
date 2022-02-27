package study.datajpa.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
class MemberRepositoryTest {
    @Autowired
    MemberRepository memberRepository;

    @Test
    public void testMember() throws Exception {
        // given
        Member member = new Member("memberA");

        // when
        Member save = memberRepository.save(member);
        Member findMember = memberRepository.findById(save.getId()).get();

        // then
        Assertions.assertEquals(save.getId(), findMember.getId());
        Assertions.assertEquals(save.getUsername(), findMember.getUsername());
        Assertions.assertEquals(save, findMember);
    }

    @Test
    @DisplayName("CRUD테스트")
    public void basicCRUD() throws Exception {
        // given
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberRepository.save(member1);
        memberRepository.save(member2);

        // when
        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();
        List<Member> all = memberRepository.findAll();
        Long count = memberRepository.count();

        // then
        assertEquals(member1, findMember1);
        assertEquals(member2, findMember2);

        assertEquals(all.size(), 2);

        assertEquals(count, 2);

        memberRepository.delete(member1);
        memberRepository.delete(member2);

        Long deleteCount = memberRepository.count();
        assertEquals(deleteCount, 0);
    }

    @Test
    @DisplayName("인터페이스만으로 동작하나 확인")
    public void findByUsernameAndAgeGreaterThen() throws Exception {
        // given
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("BBB", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        // when
        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("BBB",15);

        // then
        assertEquals(result.get(0).getUsername(), "BBB");
        assertEquals(result.get(0).getAge(), 20);
        assertEquals(result.size(), 1);

    }

}
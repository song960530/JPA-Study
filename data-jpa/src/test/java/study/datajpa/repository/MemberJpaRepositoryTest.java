package study.datajpa.repository;

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
class MemberJpaRepositoryTest {
    @Autowired
    MemberJpaRepository memberJpaRepository;

    @Test
    @DisplayName("기본 저장,검색기능")
    public void testMember() throws Exception {
        // given
        Member member = new Member("memberA");

        // when
        Member save = memberJpaRepository.save(member);
        Member findMember = memberJpaRepository.find(save.getId());

        // then
        assertEquals(save.getId(), findMember.getId());
        assertEquals(save.getUsername(), findMember.getUsername());
        assertEquals(save, findMember);
    }

    @Test
    @DisplayName("CRUD테스트")
    public void basicCRUD() throws Exception {
        // given
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberJpaRepository.save(member1);
        memberJpaRepository.save(member2);

        // when
        Member findMember1 = memberJpaRepository.findById(member1.getId()).get();
        Member findMember2 = memberJpaRepository.findById(member2.getId()).get();
        List<Member> all = memberJpaRepository.findAll();
        Long count = memberJpaRepository.count();

        // then
        assertEquals(member1, findMember1);
        assertEquals(member2, findMember2);

        assertEquals(all.size(), 2);

        assertEquals(count, 2);

        memberJpaRepository.delete(member1);
        memberJpaRepository.delete(member2);

        Long deleteCount = memberJpaRepository.count();
        assertEquals(deleteCount, 0);
    }

    @Test
    public void 벌크업데이트() throws Exception {
        // given
        memberJpaRepository.save(new Member("member1", 10));
        memberJpaRepository.save(new Member("member2", 19));
        memberJpaRepository.save(new Member("member3", 20));
        memberJpaRepository.save(new Member("member4", 21));
        memberJpaRepository.save(new Member("member5", 40));

        // when
        int resultCount = memberJpaRepository.bulkAgePlus(20);

        // then
        assertEquals(resultCount, 3);
    }
}
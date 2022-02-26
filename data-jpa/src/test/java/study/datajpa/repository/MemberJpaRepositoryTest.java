package study.datajpa.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;

@SpringBootTest
@Transactional
class MemberJpaRepositoryTest {
    @Autowired
    MemberJpaRepository memberJpaRepository;

    @Test
    public void testMember() throws Exception {
        // given
        Member member = new Member("memberA");

        // when
        Member save = memberJpaRepository.save(member);
        Member findMember = memberJpaRepository.find(save.getId());

        // then
        Assertions.assertEquals(save.getId(), findMember.getId());
        Assertions.assertEquals(save.getUsername(), findMember.getUsername());
        Assertions.assertEquals(save, findMember);
    }

}
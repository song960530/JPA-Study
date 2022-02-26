package study.datajpa.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;

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

}
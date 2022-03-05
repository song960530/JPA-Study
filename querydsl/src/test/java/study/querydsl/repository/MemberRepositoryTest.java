package study.querydsl.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.entity.Member;

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberRepositoryTest {

    @Autowired
    EntityManager em;

    @Autowired
    MemberRepository memberRepository;

    @Test
    @DisplayName("기본테스트")
    public void basicTest() throws Exception {
        // given
        Member member = new Member("member1", 10);
        memberRepository.save(member);

        // when
        Member result1 = memberRepository.findById(member.getId()).orElseGet(null);
        List<Member> result2 = memberRepository.findAll();
        List<Member> result3 = memberRepository.findByUsername(member.getUsername());

        // then
        assertEquals(result1, member);
        assertThat(result2).containsExactly(member);
        assertThat(result3).containsExactly(member);
    }

}
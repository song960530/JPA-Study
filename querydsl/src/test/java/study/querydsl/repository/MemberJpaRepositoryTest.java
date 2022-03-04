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
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
class MemberJpaRepositoryTest {

    @Autowired
    EntityManager em;

    @Autowired
    MemberJpaRepository repository;

    @Test
    @DisplayName("기본테스트")
    public void basicTest() throws Exception {
        // given
        Member member = new Member("member1", 10);
        repository.save(member);

        // when
        Member result1 = repository.findById(member.getId()).orElseGet(null);
        List<Member> result2 = repository.findAll();
        List<Member> result3 = repository.findByUsername(member.getUsername());

        // then
        assertEquals(result1, member);
        assertThat(result2).containsExactly(member);
        assertThat(result3).containsExactly(member);
    }

    @Test
    @DisplayName("기본테스트_querydsl")
    public void basicTest_querydsl() throws Exception {
        // given
        Member member = new Member("member1", 10);
        repository.save(member);

        // when
        Member result1 = repository.findById(member.getId()).orElseGet(null);
        List<Member> result2 = repository.findAll_querydsl();
        List<Member> result3 = repository.findByUsername_querydsl(member.getUsername());

        // then
        assertEquals(result1, member);
        assertThat(result2).containsExactly(member);
        assertThat(result3).containsExactly(member);
    }
}
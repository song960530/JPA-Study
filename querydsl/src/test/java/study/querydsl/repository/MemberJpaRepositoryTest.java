package study.querydsl.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.dto.MemberSearchCondition;
import study.querydsl.dto.MemberTeamDto;
import study.querydsl.entity.Member;
import study.querydsl.entity.Team;

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
    MemberJpaRepository memberJpaRepository;

    @Autowired
    TeamRepository teamRepository;

    @Autowired
    MemberRepository memberRepository;

    @Test
    @DisplayName("기본테스트")
    public void basicTest() throws Exception {
        // given
        Member member = new Member("member1", 10);
        memberJpaRepository.save(member);

        // when
        Member result1 = memberJpaRepository.findById(member.getId()).orElseGet(null);
        List<Member> result2 = memberJpaRepository.findAll();
        List<Member> result3 = memberJpaRepository.findByUsername(member.getUsername());

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
        memberJpaRepository.save(member);

        // when
        Member result1 = memberJpaRepository.findById(member.getId()).orElseGet(null);
        List<Member> result2 = memberJpaRepository.findAll_querydsl();
        List<Member> result3 = memberJpaRepository.findByUsername_querydsl(member.getUsername());

        // then
        assertEquals(result1, member);
        assertThat(result2).containsExactly(member);
        assertThat(result3).containsExactly(member);
    }

    @Test
    @DisplayName("querydsl_search_BooleanBuilder사용")
    public void search_booleanbuilder() throws Exception {
        // given
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);
        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);
        memberRepository.save(member1);
        memberRepository.save(member2);
        memberRepository.save(member3);
        memberRepository.save(member4);

        MemberSearchCondition condition = new MemberSearchCondition();
        condition.setAgeGoe(35);
        condition.setAgeLoe(40);
        condition.setTeamName("teamB");

        // when
        List<MemberTeamDto> result = memberJpaRepository.searchByBuilder(condition);

        // then
        assertThat(result).extracting("userName").containsExactly("member4");
    }
    @Test
    @DisplayName("querydsl_search_Where다중파라미터 사용")
    public void search_Where() throws Exception {
        // given
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);
        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);
        memberRepository.save(member1);
        memberRepository.save(member2);
        memberRepository.save(member3);
        memberRepository.save(member4);

        MemberSearchCondition condition = new MemberSearchCondition();
        condition.setAgeGoe(35);
        condition.setAgeLoe(40);
        condition.setTeamName("teamB");

        // when
        List<MemberTeamDto> result = memberJpaRepository.searchByWhere(condition);

        // then
        assertThat(result).extracting("userName").containsExactly("member4");
    }
}
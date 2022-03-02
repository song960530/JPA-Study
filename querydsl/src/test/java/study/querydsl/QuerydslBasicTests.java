package study.querydsl;

import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.entity.Member;
import study.querydsl.entity.QMember;
import study.querydsl.entity.Team;
import study.querydsl.repository.MemberRepository;
import study.querydsl.repository.TeamRepository;

import javax.persistence.EntityManager;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static study.querydsl.entity.QMember.member;

@SpringBootTest
@Transactional
class QuerydslBasicTests {
    @Autowired
    EntityManager em;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    TeamRepository teamRepository;

    JPAQueryFactory queryFactory;

    @BeforeEach
    public void init() {
        queryFactory = new JPAQueryFactory(em);
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
    }

    @Test
    public void queryDslTest() throws Exception {
        // given

        // when
        Member findMember = queryFactory
                .select(member)
                .from(member)
                .where(member.username.eq("member1")).fetchOne();

        // then
        assertEquals(findMember.getUsername(), "member1");
    }

    @Test
    public void search() throws Exception {
        // given

        // when
        Member findMember = queryFactory
                .selectFrom(member)
                .where(
                        member.username.eq("member1")
                        , member.age.eq(10)
                )
                .fetchOne();

        // then
        assertEquals(findMember.getUsername(), "member1");
    }

    @Test
    @DisplayName("다양한 결과조회 방법")
    public void resultFetch() throws Exception {
        // given

        // when
        List<Member> fetch = queryFactory.selectFrom(member).fetch(); // 리스트 조회
//        Member fetchOne = queryFactory.selectFrom(QMember.member).fetchOne(); // 단건 조회
        Member fetchFirst = queryFactory.selectFrom(QMember.member).fetchFirst(); // 첫번째 값 조회

        QueryResults<Member> results = queryFactory.selectFrom(member).fetchResults(); // 페이징 정보 포함 조회
        List<Member> result = results.getResults(); // contents 보는법
        long total = results.getTotal();// count 보는법

        long count = queryFactory.selectFrom(member).fetchCount(); // count 조회

        // then
    }

    /**
     * 회원 정렬 순서
     * 1. 회원 나이 내림차순
     * 2. 회원 이름 올림차순
     * 단 2에서 회원 이름이 없으면 마지막에 출력(nulls last)
     */
    @Test
    @DisplayName("정렬 테스트")
    public void sort() throws Exception {
        // given
        memberRepository.save(new Member(null, 100));
        memberRepository.save(new Member("member5", 100));
        memberRepository.save(new Member("member6", 100));

        // when
        List<Member> result = queryFactory.selectFrom(member)
                .where(member.age.eq(100))
                .orderBy(member.age.desc(), member.username.asc().nullsLast())
                .fetch();

        // then
        assertEquals(result.get(0).getUsername(), "member5");
        assertEquals(result.get(1).getUsername(), "member6");
        assertEquals(result.get(2).getUsername(), null);
    }

    @Test
    @DisplayName("페이징 테스트")
    public void paging1() throws Exception {
        // given

        // when
        QueryResults<Member> result = queryFactory
                .selectFrom(member)
                .orderBy(member.username.desc())
                .offset(0)
                .limit(2)
                .fetchResults();

        // then
        assertEquals(result.getResults().size(), 2);
        assertEquals(result.getResults().get(0).getUsername(), "member4");
        assertEquals(result.getTotal(), 4);
        assertEquals(result.getLimit(), 2);
        assertEquals(result.getOffset(), 0);
    }
}

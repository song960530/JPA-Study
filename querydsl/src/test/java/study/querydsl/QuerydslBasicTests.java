package study.querydsl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.dto.MemberDto;
import study.querydsl.dto.QMemberDto;
import study.querydsl.dto.UserDto;
import study.querydsl.entity.Member;
import study.querydsl.entity.QMember;
import study.querydsl.entity.Team;
import study.querydsl.repository.MemberRepository;
import study.querydsl.repository.TeamRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static study.querydsl.entity.QMember.member;
import static study.querydsl.entity.QTeam.team;

@SpringBootTest
@Transactional
class QuerydslBasicTests {
    @PersistenceUnit
    EntityManagerFactory emf;
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

    @Test
    @DisplayName("집합(Tuple 사용법)")
    public void aggregation() throws Exception {
        // given

        // when
        List<Tuple> result = queryFactory
                .select(
                        member.count()
                        , member.age.sum()
                        , member.age.avg()
                        , member.age.max()
                        , member.age.min()
                )
                .from(member)
                .fetch();

        Tuple tuple = result.get(0);

        // then
        assertEquals(tuple.get(member.count()), 4);
        assertEquals(tuple.get(member.age.sum()), 100);
        assertEquals(tuple.get(member.age.avg()), 25);
        assertEquals(tuple.get(member.age.max()), 40);
        assertEquals(tuple.get(member.age.min()), 10);
    }

    /**
     * 팀의 이름과 각 팀의 평균 연령을 구하라
     */
    @DisplayName("GroupBY 사용법")
    @Test
    public void groupByTest() throws Exception {
        // given

        // when
        List<Tuple> result = queryFactory
                .select(team.name, member.age.avg())
                .from(member)
                .join(member.team, team)
                .groupBy(team.name)
                .fetch();

        Tuple teamA = result.get(0);
        Tuple teamB = result.get(1);

        // then
        assertEquals(teamA.get(team.name), "teamA");
        assertEquals(teamA.get(member.age.avg()), 15);

        assertEquals(teamB.get(team.name), "teamB");
        assertEquals(teamB.get(member.age.avg()), 35);
    }

    @DisplayName("기본 조인 테스트")
    @Test
    public void joinTest() throws Exception {
        // given

        // when
        List<Member> result = queryFactory
                .selectFrom(member)
                .leftJoin(member.team, team)
                .where(team.name.eq("teamA"))
                .orderBy(member.username.asc())
                .fetch();

        // then
        assertEquals(result.get(0).getUsername(), "member1");
        assertEquals(result.get(1).getUsername(), "member2");
    }

    /**
     * 회원과 팀을 조인하면서 팀 이름이 teamA인 팀만 조인, 회원은 모두 조회
     * JPQL : select m,t from Member m left join m.team t on t.name = 'teamA'
     */
    @DisplayName("join 테스트")
    @Test
    public void joinOnFiltering() throws Exception {
        // given

        // when
        List<Tuple> result = queryFactory
                .select(member, team)
                .from(member)
                .leftJoin(member.team, team)
                .on(team.name.eq("teamA"))
                .fetch();

        // then
        for (Tuple tuple : result) {
            System.out.println("tuple = " + tuple);
        }

        assertEquals(result.get(2).get(team), null);
        assertEquals(result.get(3).get(team), null);
    }

    /**
     * 연관관계가 없는 엔티티 외부 조인
     * 회원의 이름이 팀 이름과 같은 대상 외부 조인
     */
    @DisplayName("연관관계가 없는 엔티티 join 테스트")
    @Test
    public void joinOnNoRelation() throws Exception {
        // given
        memberRepository.save(new Member("teamA"));
        memberRepository.save(new Member("teamB"));
        memberRepository.save(new Member("teamC"));

        // when
        List<Tuple> result = queryFactory
                .select(member, team)
                .from(member)
                .leftJoin(team)
                .on(member.username.eq(team.name))
                .fetch();

        // then
        for (Tuple tuple : result) {
            System.out.println("tuple = " + tuple);
        }
    }

    @Test
    public void fetchJoinTest() throws Exception {
        // given
        em.flush();
        em.clear();

        // when
        Member findMember = queryFactory
                .selectFrom(member)
                .join(member.team, team).fetchJoin()
                .where(member.username.eq("member1"))
                .fetchOne();

        // then
        assertEquals(emf.getPersistenceUnitUtil().isLoaded(findMember.getTeam()), true);
    }

    /**
     * 나이가 가장 많은 회원 조회
     */
    @DisplayName("서브쿼리 사용법eq")
    @Test
    public void subQuery() throws Exception {
        // given
        QMember memberSub = new QMember("memberSub");

        // when
        List<Member> result = queryFactory
                .selectFrom(member)
                .where(member.age.eq(
                        JPAExpressions
                                .select(memberSub.age.max())
                                .from(memberSub)
                ))
                .fetch();

        // then
        assertEquals(result.get(0).getAge(), 40);
    }

    /**
     * 나이가 가장 많은 회원 조회
     */
    @DisplayName("서브쿼리 사용법goe")
    @Test
    public void subQueryGOe() throws Exception {
        // given
        QMember memberSub = new QMember("memberSub");

        // when
        List<Member> result = queryFactory
                .selectFrom(member)
                .where(member.age.goe(
                        JPAExpressions
                                .select(memberSub.age.avg())
                                .from(memberSub)
                ))
                .fetch();

        // then
        assertThat(result).extracting("age").contains(30, 40);
    }

    @DisplayName("서브쿼리 사용법 select절")
    @Test
    public void subQuerySelect() throws Exception {
        // given
        QMember memberSub = new QMember("memberSub");

        // when
        List<Tuple> result = queryFactory
                .select(member.username,
                        JPAExpressions
                                .select(memberSub.age)
                                .from(memberSub)
                                .where(memberSub.username.eq(member.username))
                )
                .from(member)
                .fetch();

        // then
        for (Tuple tuple : result) {
            System.out.println("tuple = " + tuple);
        }
    }

    @DisplayName("간단한 case 작성법")
    @Test
    public void simpleCase() throws Exception {
        // given

        // when
        List<String> result = queryFactory
                .select(member.age
                        .when(10).then("열살")
                        .when(20).then("스무살")
                        .otherwise("기타")
                )
                .from(member)
                .fetch();

        // then
        for (String s : result) {
            System.out.println("s = " + s);
        }
    }

    @DisplayName("복잡한 case 작성법")
    @Test
    public void complexCase() throws Exception {
        // given

        // when
        List<String> result = queryFactory
                .select(new CaseBuilder()
                        .when(member.age.between(10, 20)).then("10~20살")
                        .when(member.age.between(21, 30)).then("21~~30살")
                        .otherwise("기타")
                )
                .from(member)
                .fetch();

        // then
        for (String s : result) {
            System.out.println("s = " + s);
        }
    }

    @DisplayName("문자열 합치기")
    @Test
    public void concat() throws Exception {
        // given

        // when
        String s = queryFactory
                .select(
                        member.username.concat("_").concat(member.age.stringValue())
                )
                .from(member)
                .where(member.username.eq("member1"))
                .fetchOne();

        // then
//        assertEquals("member1_10", s);
        assertEquals("member1_1", s); // H2 concat 오류로 인하여 문자열 한개만 캐스팅됨
    }

    @DisplayName("DTO로 조회하는 방법_setter")
    @Test
    public void findDtoBySetter() throws Exception {
        // given

        // when
        List<MemberDto> result = queryFactory
                .select(Projections.bean(MemberDto.class,
                        member.username,
                        member.age
                ))
                .from(member)
                .fetch();

        // then
        assertEquals("member1", result.get(0).getUsername());

    }

    @DisplayName("DTO로 조회하는 방법_field")
    @Test
    public void findDtoByField() throws Exception {
        // given

        // when
        List<UserDto> result = queryFactory
                .select(Projections.fields(UserDto.class,
                        member.username.as("name"),
                        member.age
                ))
                .from(member)
                .fetch();

        // then
        assertEquals("member1", result.get(0).getName());

    }

    @DisplayName("DTO로 조회하는 방법_constructor")
    @Test
    public void findDtoByConstructor() throws Exception {
        // given

        // when
        List<MemberDto> result = queryFactory
                .select(Projections.constructor(MemberDto.class,
                        member.username,
                        member.age
                ))
                .from(member)
                .fetch();

        // then
        assertEquals("member1", result.get(0).getUsername());

    }

    @DisplayName("DTO로 조회하는 방법_@QueryProjection")
    @Test
    public void findDtoByQueryProjection() throws Exception {
        // given

        // when
        List<MemberDto> result = queryFactory
                .select(new QMemberDto(
                                member.username,
                                member.age
                        )
                )
                .from(member)
                .fetch();

        // then
        assertEquals("member1", result.get(0).getUsername());
    }

    @DisplayName("DTO로 조회하는 방법_@QueryProjection2")
    @Test
    public void findDtoByQueryProjection2() throws Exception {
        // given

        // when
        List<MemberDto> result = queryFactory
                .select(new QMemberDto(
                                member.username
                        )
                )
                .from(member)
                .fetch();

        // then
        assertEquals("member1", result.get(0).getUsername());
    }

    @Test
    @DisplayName("BooleanBuilder를 사용한 동적 쿼리")
    public void dynamicQuery_booleanuBuilder() throws Exception {
        // given
        String usernameParam = "member1";
        Integer ageParam = 10;

        // when
        List<Member> result = searchMember1(usernameParam, ageParam);

        // then
        assertEquals(usernameParam, result.get(0).getUsername());
        assertEquals(ageParam, result.get(0).getAge());

    }

    private List<Member> searchMember1(String usernameParam, Integer ageParam) {

        BooleanBuilder builder = new BooleanBuilder();

        if (usernameParam != null) builder.and(member.username.eq(usernameParam));
        if (ageParam != null) builder.and(member.age.eq(ageParam));

        return queryFactory
                .selectFrom(member)
                .where(builder)
                .fetch();

    }

    @Test
    @DisplayName("Where 다중파라미터를 사용한 동적 쿼리")
    public void dynamicQuery_where() throws Exception {
        // given
        String usernameParam = "member1";
        Integer ageParam = 10;

        // when
        List<Member> result = searchMember2(usernameParam, ageParam);

        // then
        assertEquals(usernameParam, result.get(0).getUsername());
        assertEquals(ageParam, result.get(0).getAge());

    }

    private List<Member> searchMember2(String usernameParam, Integer ageParam) {
        return queryFactory
                .selectFrom(member)
//                .where(userNameEq(usernameParam), ageEq(ageParam))
                .where(allEq(usernameParam, ageParam))
                .fetch();
    }

    private BooleanExpression ageEq(Integer ageParam) {
        return ageParam != null ? member.age.eq(ageParam) : null;
    }


    private BooleanExpression userNameEq(String usernameParam) {
        return usernameParam != null ? member.username.eq(usernameParam) : null;
    }

    private BooleanExpression allEq(String usernameParam, Integer ageParam) {
        return userNameEq(usernameParam).and(ageEq(ageParam));
    }

}

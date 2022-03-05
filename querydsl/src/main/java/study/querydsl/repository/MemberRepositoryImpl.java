package study.querydsl.repository;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import study.querydsl.dto.MemberSearchCondition;
import study.querydsl.dto.MemberTeamDto;
import study.querydsl.dto.QMemberTeamDto;
import study.querydsl.entity.Member;

import java.util.List;

import static study.querydsl.entity.QMember.member;
import static study.querydsl.entity.QTeam.team;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<MemberTeamDto> search(MemberSearchCondition condition) {
        return jpaQueryFactory
                .select(new QMemberTeamDto(
                        member.id.as("memberId")
                        , member.username.as("userName")
                        , member.age
                        , member.team.id.as("teamId")
                        , member.team.name.as("teamName")
                ))
                .from(member)
                .join(member.team, team)
                .where(
                        userNameEq(condition.getUserName())
                        , teamNameEq(condition.getTeamName())
                        , ageGoe(condition.getAgeGoe())
                        , ageLoe(condition.getAgeLoe())
                )
                .fetch();
    }

    @Override
    public Page<MemberTeamDto> searchPageSimple(MemberSearchCondition condition, Pageable pageable) {
        QueryResults<MemberTeamDto> result = jpaQueryFactory
                .select(new QMemberTeamDto(
                        member.id.as("memberId")
                        , member.username.as("userName")
                        , member.age
                        , member.team.id.as("teamId")
                        , member.team.name.as("teamName")
                ))
                .from(member)
                .join(member.team, team)
                .where(
                        userNameEq(condition.getUserName())
                        , teamNameEq(condition.getTeamName())
                        , ageGoe(condition.getAgeGoe())
                        , ageLoe(condition.getAgeLoe())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        List<MemberTeamDto> content = result.getResults();
        long total = result.getTotal();

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<MemberTeamDto> searchPageComplex(MemberSearchCondition condition, Pageable pageable) {
        List<MemberTeamDto> result = jpaQueryFactory
                .select(new QMemberTeamDto(
                        member.id.as("memberId")
                        , member.username.as("userName")
                        , member.age
                        , member.team.id.as("teamId")
                        , member.team.name.as("teamName")
                ))
                .from(member)
                .join(member.team, team)
                .where(
                        userNameEq(condition.getUserName())
                        , teamNameEq(condition.getTeamName())
                        , ageGoe(condition.getAgeGoe())
                        , ageLoe(condition.getAgeLoe())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Member> countQuery = jpaQueryFactory
                .selectFrom(member)
                .join(member.team, team)
                .where(
                        userNameEq(condition.getUserName())
                        , teamNameEq(condition.getTeamName())
                        , ageGoe(condition.getAgeGoe())
                        , ageLoe(condition.getAgeLoe())
                );

//        return PageableExecutionUtils.getPage(result, pageable, result::size);
        return PageableExecutionUtils.getPage(result, pageable, countQuery::fetchCount);
//        return new PageImpl<>(result, pageable, total);
    }

    private BooleanExpression ageLoe(Integer ageLoe) {
        return ageLoe != null ? member.age.loe(ageLoe) : null;
    }

    private BooleanExpression ageGoe(Integer ageGoe) {
        return ageGoe != null ? member.age.goe(ageGoe) : null;
    }

    private BooleanExpression teamNameEq(String teamName) {
        return StringUtils.hasText(teamName) ? team.name.eq(teamName) : null;
    }

    private BooleanExpression userNameEq(String userName) {
        return StringUtils.hasText(userName) ? member.username.eq(userName) : null;
    }

}

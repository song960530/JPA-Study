package study.datajpa.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import javax.persistence.EntityManager;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
class MemberRepositoryTest {
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    TeamRepository teamRepository;
    @Autowired
    EntityManager em;

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
        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("BBB", 15);

        // then
        assertEquals(result.get(0).getUsername(), "BBB");
        assertEquals(result.get(0).getAge(), 20);
        assertEquals(result.size(), 1);

    }

    @Test
    public void testQuery() throws Exception {
        // given
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("BBB", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        // when
        List<Member> result = memberRepository.findUser("BBB", 20);

        // then
        assertEquals(result.get(0).getUsername(), "BBB");
        assertEquals(result.get(0).getAge(), 20);
        assertEquals(result.size(), 1);
    }

    @Test
    public void findUsernameList() throws Exception {
        // given
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("BBB", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        // when
        List<String> result = memberRepository.findUsernameList();

        // then
        assertEquals(result.get(0), "AAA");
        assertEquals(result.get(1), "BBB");
    }

    @Test
    public void findMemberDto() throws Exception {
        // given
        Team teamA = new Team("teamA");
        teamRepository.save(teamA);

        Member member1 = new Member("AAA", 10);
        member1.setTeam(teamA);
        memberRepository.save(member1);

        // when
        List<MemberDto> memberDtos = memberRepository.findMemberDto();

        // then
        assertEquals(memberDtos.get(0).getTeamName(), "teamA");
        assertEquals(memberDtos.get(0).getUsername(), "AAA");
    }

    @Test
    public void findByNames() throws Exception {
        // given
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("BBB", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        // when
        List<Member> result = memberRepository.findByNames(Arrays.asList("AAA", "BBB"));

        // then
        assertEquals(result.get(0).getUsername(), "AAA");
        assertEquals(result.get(1).getUsername(), "BBB");
    }

    @Test
    public void findTest() throws Exception {
        // given
        Member member1 = new Member("AAA", 10);
        memberRepository.save(member1);

        // when
        List<Member> result1 = memberRepository.findListByUsername("AAA");
        Member result2 = memberRepository.findMemberByUsername("AAA");
        Optional<Member> result3 = memberRepository.findOptionalByUsername("AAA");
    }

    @Test
    public void paging() throws Exception {
        // given
        Member member1 = new Member("Member1", 10);
        Member member2 = new Member("Member2", 10);
        Member member3 = new Member("Member3", 10);
        Member member4 = new Member("Member4", 10);
        Member member5 = new Member("Member5", 10);

        memberRepository.save(member1);
        memberRepository.save(member2);
        memberRepository.save(member3);
        memberRepository.save(member4);
        memberRepository.save(member5);

        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        // when
        Page<Member> result = memberRepository.findByAge(10, pageRequest);
        List<Member> content = result.getContent();
        long count = result.getTotalElements();

        Page<MemberDto> toMap = result.map(m -> new MemberDto(m));

        // then
        for (Member member : content) {
            System.out.println("member = " + member);
        }
        assertEquals(content.size(), 3);
        assertEquals(count, 5);
        assertEquals(result.getNumber(), 0);
        assertEquals(result.isFirst(), true);
        assertEquals(result.hasNext(), true);
    }

    @Test
    public void 벌크업데이트() throws Exception {
        // given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 19));
        memberRepository.save(new Member("member3", 20));
        memberRepository.save(new Member("member4", 21));
        memberRepository.save(new Member("member5", 40));

        // when
        int resultCount = memberRepository.bulkAgePlus(20);

//        em.clear();

        Member findMember = memberRepository.findMemberByUsername("member5");

        // then
        assertEquals(resultCount, 3);
        assertEquals(findMember.getAge(), 41);
    }

    @Test
    public void EntityGraph_테스트() throws Exception {
        // given
        Team teamA = new Team("teamA");
        teamRepository.save(teamA);
        memberRepository.save(new Member("member1", 10, teamA));
        memberRepository.save(new Member("member2", 19, teamA));
        memberRepository.save(new Member("member3", 20, teamA));
        memberRepository.save(new Member("member4", 21, teamA));
        memberRepository.save(new Member("member5", 40, teamA));

        // when
//        List<Member> findAll = memberRepository.findAll();
        List<Member> findAll = memberRepository.findEntityGraphByAgeGreaterThanEqual(10);

        // then
        for (Member member : findAll) {
            System.out.println("member = " + member.getTeam().getName());
        }
    }

    @Test
    public void QueryHint테스트() throws Exception {
        // given
        Member member = new Member("member1", 10);
        memberRepository.save(member);
        em.flush();
        em.clear();

        // when
        Member findMember = memberRepository.findReadOnlyByUsername("member1");
        findMember.setUsername("asd");

        // then
    }

    @Test
    public void 커스텀_레포지토리() throws Exception {
        // given
        List<Member> memberCustom = memberRepository.findMemberCustom();
        // when
        System.out.println("memberCustom = " + memberCustom);
        // then
    }

    @Test
    public void BaseEntity_테스트() throws Exception {
        // given
        Member member = new Member("member");
        memberRepository.save(member);
        em.flush();
        em.clear();

        Thread.sleep(100);

        Member findMember = memberRepository.findById(member.getId()).get();
        findMember.setUsername("member2");
        em.flush();
        em.clear();

        // when


        // then
        System.out.println("findMember = " + findMember.getCreatedBy());
        System.out.println("findMember = " + findMember.getCreateTime());
        System.out.println("findMember = " + findMember.getLastModifiedBy());
        System.out.println("findMember = " + findMember.getUpdateTime());
    }

    @Test
    public void 네이티브쿼리_테스트() throws Exception {
        // given
        Member member = new Member("member");
        memberRepository.save(member);

        em.flush();
        em.clear();

        // when
        Member findMember = memberRepository.findByNativeQuery("member");

        // then
        assertEquals(findMember.getUsername(), member.getUsername());
    }
}
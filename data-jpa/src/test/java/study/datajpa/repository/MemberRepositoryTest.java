package study.datajpa.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

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
}
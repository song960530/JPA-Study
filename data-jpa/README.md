
# Spring Data JPA 내용 정리

## 설정의 자동화
- 순수 JPA를 사용하였을때 설정하였던 persistence.xml, LocalContainerEntityManagerFactoryBean 같은 설정들이  
모두 스프링 부트를 통하여 자동화되어 설정할 필요가 없다

## Entity에서 롬복 사용시 주의사항
- 가급적 실무에선 @Setter은 사용하지 않기
- JPA스펙상 기본 생성자를 막을 순 없다
  - @NoArgsConstructor(AccessLevel.PROTECTED) 
  - 생성자 접근 제어자를 protected로 선언
- @ToString사용시엔 연관관계가 없는 필드만 출력되도록 설정한다 (잘못 사용할 경우 StackOverFlow 장애 발생)
  - @ToString(of = {"id","username".....}) 

## JPA 공통 인터페이스 적용법

```java
// 제네릭 타입은 <T, ID> 이다
// Spring Data JPA가 자동으로 컴포넌트 스캔을 처리하기 때문에 @Repository 생략 가능
public interface memberRepository extends JpaRepository<Member,Long> { 
  ...
```

### 쿼리 메소드 기능 3가지
- 메소드 이름으로 쿼리 생성
- 메소드 이름으로 JPA NamedQuery 호출
- @Query 사용하여 인터페이스에 쿼리 직접 정의

# 메소드 이름으로 쿼리 생성
- 메소드 이름으로 생성시 아래와 같은 규칙을 따라야한다
  - 조회 : find....By, read....By, query....By, get....By
  - 카운트 : count....By
  - 삭제 : delete...By, remove....By
  - Distinct : findDistinct, findMemberDistinctBy
  - Limit : findFirst, findTop, fnidTop3
```java
// 특정 이름과 특정 나이보다 큰 멤버를 조회한다
List<Member> findMembersByUsernameAndAgeGreaterThan (String username, int age);
```
- 엔티티의 필드명이 변경되면 인터페이스에 정의한 메서드 이름도 변경해줘야한다
  - 어플리케이션 실행 시점에 오류 발생
- 자세한 조건들은 공식문서 참고
  - https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#jpa.query-methods.query-creation
  - https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories.query-methods.query-creation
  - https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories.limit-query-result

# JPA NamedQuery
- 엔티티의 상단에 쿼리를 미리 작성해두고 필요한 부분에서 가져다 쓰는 방식이다
- 엔티티가 더러워질 수 있고, 쿼리의 직관석이 떨어질 우려가 있으므로 권장하지 않는다
```java
// NamedQuery 선언부
@Entity
@NamedQuery(
  name="Member.findByUsername",
  query="select m from Member m where m.username = :username"
)
public class Member {
  ...
```

```java
// 이렇게 사용하는 방법이 있고
List<Member> result = em.createQuery("Member.findByUsername",Member.class).setParameter("username",username).getResultList();
  
// 이렇게 사용하는 방법도 있다
@Query(name = "Member.findByUsername")
List<Member> findByUsername(@Param("username") String username);
```









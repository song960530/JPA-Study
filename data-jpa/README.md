
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

# @Query
- 실행 메소드에 정적 쿼리를 직접 작성하는 방식
- 이름 없는 Named 쿼리라 할 수 있다
- 애플리케이션 실행 시점에 문법 오류를 발견할 수 있다
```java
// Entity 직접 조회
@Query("select m from Member m where m.username = :username and m.age = :age")
List<Member> findUser(@Param("username") String username, @Param("age") int age);

// DTO 조회
@Query("select new study.datajpa.dto.MemberDto(m.id, m.username, t.name) from Member m join m.team t")
List<MemberDto> findMemberDto();
```

# 결과값 반환
- 컬렉션
  - 결과가 없으면 빈 컬렉션
- 단건
  - 결과가 없으면 null
    - JPA 에선 결과가 없으면 NoResultException 예외를 발생시키지만 Spring Data JPA가 해당 예외 발생시 null을 리턴해준다
  - 2건 이상이면 NonUniqueResultException 예외 발생

# Spring Data JPA의 페이징,정렬
-페이징 정렬 파라미터
  - Sort : 정렬 기능
  - Pageable : 페이징 기능 (내부에 Sort 포함) 
  - 리턴 타입
    - Page : count 쿼리 결과를 포함한 페이징
    - Slice : count 쿼리 없이 다음 페이지를 호출해야하는지 확인 (내부적으로 limit+1 조회, 모바일같은곳에서 스크롤로 로딩할때 자주 사용)
    - List(컬렉션) : count 쿼리 없이 결과만
```java
// count 쿼리 사용
Page<Member> findByUsername(String name, Pageable pageable); 

// count 쿼리 사용 x
Slice<Member> findByUsername(String name, Pageable pageable);

// count 쿼리 사용 x
List<Member> findByUsername(String name, Pageable pageable);

// 별도의 count 쿼리 사용
@Query(value = "select m from Member m", countQuery = "select count(m.username) from Member m")
Page<Member> findMemberAllCountBy(Pageable pageable);

// 페이지 유지하면서 엔티티 -> DTO로 변환
Page<Member> page = memberRepository.findByAge(10, pageRequest);
Page<MemberDto> dtoPage = page.map(m -> new MemberDto(m));
```

# Count쿼리 참고사항
- 전체 count쿼리는 매우 무겁다
- 복잡한 sql 사용시엔 카운트 쿼리를 별도로 불리하는게 성능최적화에 굉장히 좋다
  - count쿼리엔 left join을 안해도 된다.

# 벌크성 수정 쿼리
- 벌크 연산은 영속성 컨텍스트를 무시하고 실행된다. (영속성 컨텍스트에 있는 엔티티와 꼬일 수 있음)
- 영속성 컨텍스트에 Entity가 없는 상태에서 벌크 연산을 먼저 수행
- 부득이하게 영속성 컨텍스트에 Entity가 있으면 벌크 연산 후 영속성 컨텍스트 초기화
```java
@Modifying
@Quert("update Member m set m.age = m.age + 1 where m.age >= :age")
int bulkAgePlus(@Param("age") int age);
```

# @EntityGraph
- Spring Data JPA를 사용할 경우 JPQL없이 페치 조인을 사용할 수 있다 (JPQL + @EntityGraph 도 가능)
```java
//공통 메서드 오버라이드
@Override
@EntityGraph(attributePaths = {"team"})
List<Member> findAll();

//JPQL + 엔티티 그래프
@EntityGraph(attributePaths = {"team"})
@Query("select m from Member m")
List<Member> findMemberEntityGraph();

//메서드 이름에서 사용
@EntityGraph(attributePaths = {"team"})
List<Member> findByUsername(String username)
```

# 사용자 정의 레포지토리 구현
- Spring Data JPA 2.x 부터 레포지토리 인터페이스 이름 + Impl 대신  
사용자 정의 인터페이스명 + Impl 방식도 지원한다.  
ex) 아래 예제에서 MemberRepositoryImpl 대신 MemberRepositoryCustomImpl로 구현해도 
```java
// interface 정의
public interface MemberRepositoryCustom {
  ...
```

```java
// interface 구현
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom {
  private final EntityManager em;
  
  @Override
  ....
  
```

```java
// interface 상속
public interface memberRepository extends JpaRepository<Member,Long>, MemberRepositoryCustom {
  ...
```



# Auditing
- 등록자,등록일,수정자,수정일을 공통으로 처리할 때 사용
1. spring boot 설정 클래스에 @EnableJpaAuditing 선언
2. Entity에 @EntityListeners(AuditingEntityListener.class) 선언
```java
// 등록자, 수정자를 처리해주는 AuditorAware 스프링 빈 등록
// 실무에선 세션이나 시큐리티 로그인 정보에서ID를 받도록 한다
@Bean
public AuditorAware<String> auditorProvider() {
  return () -> Optional.of(UUID.randomUUID().toString());
}
```

```java
// 등록자 수정자가 필요없을 경우를 대비하여 분리하고 타입을 상속받는다
@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
public class BaseTimeEntity {
  @CreatedDate
  @Column(updateable = false)
  private LocalDateTime createDate;
  
  @LastModifiedDate
  private LocalDateTime lastModifiedDate;
}

@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
public class BaseEntity extends BaseTimeEntity {
  @CreatedBy
  @Column(updatable = false)
  private String createdBy;
  
  @LastModifiedBy
  private String lastModifiedBy;
}
```




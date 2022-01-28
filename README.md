# JPA-Study
## 김영한 개발자님의 "자바 ROM 표준 JPA 프로그래밍" 강의

### SQL 중심의 개발 문제점
- 무한반복(CRUD), 지루한 코드
- 필드 추가 시 수작업으로 추가해줘야함
- 진정한 의미의 계층 분할이 어렵다
- 객체답게 모델링 할수록 매핑 작업만 늘어난다

### JPA란
- Java persistence API (자바 진영의 ORM[객체 관계 매핑] 기술 표준)
- 하이버네이트(오픈소스)의 상위호환이 JPA(자바표준)이다

### JPA 개발의 장점
- SQL 중심적인 개발에서 객체 중심으로 개발
- 생상성
- 유지보수
- 패러다임의 불일치 해결
- 성능
- 데이터 접근 추상화의 벤더 독립성
- 표준

### JPA의 성능 최적화 기능
- 1차 캐시와 동일성 보장
- 트랜잭션을 지원하는 쓰기 지연
- 즉시로딩(Join SQl로 한번에 연관된 객체까지 미리 조회) 지연로딩(객체가 실제 사용될 때 로딩)
  
### JPA에서 가장 중요한 2가지
ORM (객체와 관계형 데이터베이스 매핑)
* 영속성 컨텍스트 * [중요]


### 영속성 컨텍스트
- JPA를 이해하는데 가장 중요
- 엔티티를 영구 저장하는 환경이란 뜻
- persist(entity)
- 논리적 개념
- 눈에보이지않음
- 엔티티 매니저를 통해 영속성 컨텍스트에 접근

### 영속성 컨텍스트의 이점
- 1차 캐시
- 동일성(identity)보장
- 트랜잭션을 지원하는 쓰기 지연
- 변경감지
- 지연로딩

### 엔티티의 생명주기
- 비영속 : 영속성 컨텍스트와 전혀 관계가 없는 새로운 상태
- 영속 : 영속성 컨텍스트에 관리되는 상태
- 준영속 : 영속성 컨텍스트에 저장되었다가 분리된 상태
  - em.detach(entity) -> 특정 엔티티만 준영속 상태로 전환
  - em.clear() -> 영속성 컨텍스트를 완전히 초기화(통으로)
  - em.close() -> 영속성 컨텍스트 종료
- 삭제 : 삭제된 상태

### 플러시
- 변경감지
- 수정된 엔티티 쓰기 지연 SQl 저장소에 등록
- 쓰기 지연 SQL 저장소의 쿼리를 DB에 전송 (등록, 수정, 삭제 쿼리)
- 영속성 컨텍스트를 플러시 하는 방법
  - em.flush (직접호출)
  - 트랜잭션 커밋 (자동호출)
  - JPQL 쿼리 실행 (자동호출)

### 엔티티매핑
- 객체와 테이블 매핑 : @Entity, @Table
  - @Entity
    - JPA로 매핑할땐 필수
    - 기본 생성자 필수
    - final클래스, enum, interface, inner 클래스 사용 X
    - 필드값에 final X
- 필드와 컬럼 매핑 : @Column
- 기본 키 매핑 : @Id
- 연관관계 매핑 : @ManyToOne, @JoinColumn

### 매핑 어노테이션
- @Column : 컬럼 매핑
  - name : 필드와 매핑할 테이블의 컬럼 이름
  - insertable,updatable : 등록,변경 가능 여부 (true/false)
  - nullable : null 허용 어부 설정
  - unique : Unique 제약조건을 걸때 사용 (잘 사용 안함)
  - columnDefinitaion : DB에 컬럼 정보를 직접 줄 수 있다
  - length : 문자 길이 제약 조건
  - percision,scale : BigDecimal 타입에서 사용
- @Temporal : 날짜 매핑
  - java8이상부턴 LocalData, LocalDataTime을 지원해주기 때문에 생략 가능
- @Enumerated : enum 타입 매핑
- @Log : BLOB, CLOB매핑
  - 문자면 CLOB, 나머진 BLOB
- @Transient : 특정 필드의 컬럼 매핑을 제외

### 기본키 매핑
- @GeneratedValue
- @SequenceGenerator
- @TaableGenerator

### 연관관계 매핑
- 객체를 테이블에 맞춰 데이터 중심으로 모델링하면 협력관게를 만들 수 없다
- 단방향 연관관계 (N -> 1)
  - @ManyToone(객체의 참조)
  - @JoinColumn(외리키 매핑)

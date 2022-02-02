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
- 양방향 연관관계 (1 -> N)
  - @OneToMany
  - 단방향 매핑만으로도 이미 매핑 완료
  - 설계단계에선 단뱡향으로 설계 후 필요에 따라 개발단계에서 추가해주면 됨 (JPQL에서 역방향으로 탐색할 일이 많음)

### 🔥양방향 연관관계와 연관관계의 주인🔥
- 양방향 연관관계에선 JPA입장에선 연관관계의 주인쪽에만 값을 넣는게 맞지만, 객체 관계 입장에선 양쪽에 다 넣는게 맞다.  
(결국 개발할땐 양쪽에 설정하는 방향으로)
  - mappedBy
    - 객체의 두 관계중 하나를 연관관계의 주인으로 지정
    - **외래키(FK)가 있는 곳을 주인으로 정해라**    
    - **주인이 아닌쪽은 읽기만 가능**
    - 주인이 아니면 mappedBy 속성으로 주인을 지정
    - 둘 중 하나로 외래 키 관리를 해야할 경우

### 연관관계 매핑 방법
- 다대일 (N:1)
  - 단방향
    - 가장 많이 사용하는 연관관계
  - 양방향
    - 외래키가 있는 쪽이 연관관계의 주인
    - 반대편은 mappedBy
    - 양쪽을 서로 참조하도록 개발

- 일대다 (1:N)
  - 단방향
    - 일대다 단방향을 사용해야 한다면 다대일 양방향을 사용하는 방향으로 가자
    - 일(1)이 연관관계의 주인
    - 객체와 테이블의 차이때문에 반대편 테이블의 외래키를 관리하는 특이한 구조 -> 단점
    - 엔티티가 관리하는 외래키가 다른 테이블에 있음 -> 단점
    - 연관관계 관리를 위해 추가로 Update 실행 -> 단점
  - 양방향
    - 다대일 양방향을 사용하자
    - 개쌉망하는 연관관계
    - 읽기 전용 필드를 사용해서 양방향처럼 사용하는 방법
    - @JoinColumn(insertable=false, updatable=false)

- 일대일 (1:1)
  - 단방향
    - 외래키에 데이터베이스 유니크 제약조건 추가
    - 다대일 단방향 매핑과 유사
  - 양방향
    - 외래키가 있는 곳이 연관관계의 주인
    - 반대편은 mappedBy
  - 주테이블 외래키
    - 주테이블만 조회해도 대상 테이블 데이터 확인 가능 -> 장점
    - 값이 없으면 null 허용 -> 단점
  - 대상테이블 외래키
    - 일대일에서 일대다 관계로 변경할 때 테이블 구조 유지 -> 장점
    - 프록시 기능의 한계로 지연로딩으로 설정해도 항상 즉시로딩 -> 단점

- 다대다 (N:N)
  - **쓰지마세요** 
  - RDB는 정규화된 테이블 2개로 다대다 관계를 표현할 수 없어서 중간에 테이블을 추가하여 일대다, 다대일 관계로 풀어냄


    

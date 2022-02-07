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

------------------------------------------------------------------------------------------------------------------------------------------------------------

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

------------------------------------------------------------------------------------------------------------------------------------------------------------

### 상속관계 매핑
  - 관계형 DB는 상속관계가 아닌 슈퍼타입 서브타입 관계라는 모델링 기법을 사용함 (객체 상속과 유사한 방법)
  - 객체의 상속 구조와 DB의 슈퍼,서브타입 관계를 매핑 == 슈퍼,서브타입 논리모델을 실제 물리모델로 구현하는 방법
  - 아래와 같이 3개의 전략이 있다. (조인전략, 단일테이블 전략, 구현클래스마다 테이블 전략)
  - @DiscriminatorColumn(name="DTYPE") -> DTYPE 컬럼을 추가하고싶을 때. (단일테이블 전략은 Default임)
  - @DiscriminatorValue("VALUE") -> DTYPE 컬럼의 값에 어떻게 저장할지 설정. (Default 값은 Entity명)

### 조인전략
![Join 전략](https://user-images.githubusercontent.com/52727315/152667271-33c2905f-e181-410f-9a4b-a8af435f3e7b.png)
  - @Inheritance(strategy=InheritanceType.JOINED)
  - 장점
    - 테이블 정규화
    - 외래 키 참조 무결성 제약조건 활용가능
    - 저장공간 효율화
  - 단점
    - 조인으로 인한 성능 저하, 조회쿼리의 복잡성
    - INSERT 쿼리 여러번 호출


### 단일 테이블 전략
![단일테이블 전략](https://user-images.githubusercontent.com/52727315/152667405-86f7cc9e-8505-424c-b6a7-d39758d36516.png)
  - @Inheritance(strategy=InheritanceType.SINGLE_TABLE)
  - 장점
    - 조인없이 조회하여 성능이 빠르고 단순함
  - 단점
    - 자식 엔티티가 매핑한 컬럼은 모두 null을 허용해줘야함.
    - 테이블이 커져 조회성능이 오히려 느려질 수 있지만, 임계지점을 넘었을때의 이야기고 임계지점을 넘는 상황이 많지않음.

### 구현클래스마다 테이블
![구현클래스마다 테이블](https://user-images.githubusercontent.com/52727315/152667531-51f7b40a-a603-4260-b76e-54d94c98915e.png)
  - @Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)
  - **DB설계자와 ORM 전분가 둘 다 추천하지 않는 방식이니 지양하도록 하자**
  - 장점
    - 서브타입을 명확하게 구분해서 처리할 때 효과적
  - 단점
    - 다른 테이블과 함께 조회를해야할 경우 UNION을 사용하여 조회해야하므로 성능이슈와 쿼리의 복잡성이 높음
------------------------------------------------------------------------------------------------------------------------------------------------------------

### @MappedSuperclass
![MappedSuperclass](https://user-images.githubusercontent.com/52727315/152667896-33da1cf7-5e20-4006-8cf6-67f144777e95.png)
  - 공통 매핑 정보가 필요할 때 사용
  - 상속관계 매핑이 아님
  - 엔티티도 아니고, 테이블과 매핑되지도 않음
  - 자식 클래스에 매핑 정보만 제공하는 역할. 추상 클래스 권장
  - EX) 등록일, 수정일, 등록자, 수정자 작틍 전체 엔티티에서 공통적으로 사용하는 값들을 공통클래스로 만들때 사용

------------------------------------------------------------------------------------------------------------------------------------------------------------

### 프록시
![프록시](https://user-images.githubusercontent.com/52727315/152672908-e8c4e03e-84a3-42c6-a401-28162da8f211.png)
  - em.getReference()
  - 포록시 객체는 처음 사용할 때 한번만 초기화 한다
  - 또한 초기화할 때 프록시 객체가 실제 엔티티로 바뀌는것이 아니라 프록시 객체를 통해서 실제 엔티티에 접근하는거다
    - 프록시 객체가 실제 엔티티를 상속받는것이기 때문에 객체 타입 체크시 주의해야함 (==은 안되고 instance of로 체크해야함)
  - 영속성 컨텍스트에 찾는 엔티티가 이미 있으면 em.getReference()를 호출해도 실제 엔티티를 반환한다
  - **준영속 상태일 때, 프록시를 초기화하면 문제가 발생 LazyInitializationException 예외 발생 (실무에서 많이 발생)**
  - 프록시 확인 함수
    - PersistenceUnitUtil.isLoaded(); -> 프록시 인스턴스 초기화 여부
    - getClass().getName() -> 프록시 클래스 확인
    - Hibernate.initialize() -> 강제 초기화 (참고로 JPA 표준은 강제 초기화가 없음 표준은 member.getName() 이런식)

### 지연로딩
  - 단방향,양방향 어노테이션 속성에 featch = FetchType.LAZY로 설정 EX) @ManyToOne(featch = FetchType.LAZY)
  - **✨모든 연관관계에선 지연로딩을 사용할 것✨**
  - 즉시로딩이 필요한 경우 JPQL fetch 조인이나, 엔티티 그래프 기능을 사용

### 즉시로딩 (실무에선 즉시로딩을 그냥 사용하지 말것)
  - 단방향,양방향 어노테이션 속성에 featch = FetchType.LAZY로 설정 EX) @ManyToOne(featch = FetchType.EAGER)
  - 🔥즉시로딩을 사용하면 예상하지 못한 SQL이 발생할 수 있으며 JPQL에선 N+1 문제를 일으킨다 (하나의 쿼리를 위해 N개의 쿼리가 더 발생하는 현상)🔥
  - **🔥@ManyToOne, @OneToOne은 기본이 즉시 로딩이니 꼭 LAZY로 설정을 변경하여 사용해야함🔥**
  - @OneToMany, @ManyToMany는 기본이 지연 로딩

------------------------------------------------------------------------------------------------------------------------------------------------------------

### CASCADE - 영속성 전이
![영속성 전이](https://user-images.githubusercontent.com/52727315/152681756-bd634a8f-ae23-4798-9d88-24d8dc1d97af.png)
  - 특정 엔티티를 영속상태로 만들어 연관된 엔티티도 같이 영속상태로 만들고 싶을 때 사용한다
  - 부모 엔티티를 저장할 때 자식 엔티티도 함께 저장한다
  - 영속성 전이는 연관관계 매핑하는 것과 아무 관련이 없으며, 함께 영속화 한다는 편리함을 제공하는것이다
  - ALL, PERSIST, REMOVE, MERGE, REFRESH, DETACH를 속성으로 사용할 수 있다

### 고아객체
  - orphanRemoval = true 속성
  - 고아객체제거 : 부모와 연관관계가 끊어진 자식 엔티티를 자동으로 삭제
  - 참조하는 곳이 하나일떄 사용해야하며, 특정 엔티티가 개인으로 소유할 때 사용해야한다
  - @OneToOne, @OneToMany 에서만 사용 가능
  - CascadeType.REMOVE와 동일하게 동작

### 영속성전이 + 고아객체
  - CascadeType.ALL + orphanRemoval = true
  - 부모 엔티티를 통해 자식의 생명주기를 관리할 수 있음
  - DDD의 Aggregate Root 개념을 구현할 때 유용

------------------------------------------------------------------------------------------------------------------------------------------------------------

### 값 타입
  - 엔티티 타입과 달리 식별자가 없고 값만 있어 변경시 추적이 불가능하다
  - 값 타입은 정말 값 타입이라 판달될 때만 사용해야 하며, 엔티티와 값타입을 혼동해서 만들면 안된다
  - 값타입 비교시 인스턴스 참조값은 "==", 인스턴스 값 비교시 "eauqls()"를 사용한다
  - **🔥식별자가 있고, 지속해서 값을 추적 및 변경해야 한다면 값타입이 아닌 엔티티로 사용해아 한다🔥**
  - 값 타입 분류
    - 기본 값 타입
      - 자바 기본 타입
      - 래퍼 클래스
      - String
    - 임베디드 타입
    - 컬렉션 값 타입

### 임베디드 타입(복합 값 타입)
![임베디드 타입](https://user-images.githubusercontent.com/52727315/152797280-34b7c7a3-a1a3-46e8-86dd-3c26252ec133.png)
  - @Embeddable : 값타입을 정의하는 곳에 표시
  - @Embedded : 값 타입을 사용하는 곳에 표시
  - 재사용성이 있으며 응집도가 높다
  - 객체와 테이블간 세밀한 매핑이 가능하다
  - 임베디듵 타입을 사용하기 전과 후에 매핑하는 테이블은 같다
  - 잘 설계한 ORM 어플리케이션은 테이블수보다 클래스의 수가 더 많다
  - 임베디드 타입의 값이 null 이면 내부 컬럼값도 모두 null이다

### 속성 재정의
  - 동일한 타입(EX.Address 클래스 2회이상 사용)의 임베디드 타입을 사용하고 싶을 경우에 사용하는 어노테이션
  - @AttributeOverrides, @AttributeOverride를 사용하여 컬럼명 속성을 재정의

### 주의 사항
  - 임베디드 같은 값타입을 여러 엔티티에서 공유하면 위험함 (Side Effect 발생)
  - Primitive Type의 경우 문제가 되지 않지만 Reference Type의 경우 문제가 된다
  - Reference Type의 경우 불변 객체로 설계해서 사용하면 부작용을 차단할 수 있다 (Setter를 사용하지 않는 등 생성이후 값이 변경되지 못하도록)

------------------------------------------------------------------------------------------------------------------------------------------------------------

# ✔활용 1편 내용

## 동작 화면
![동작화면](https://user-images.githubusercontent.com/52727315/156873281-e1b9975e-8ac8-42c5-bda2-4606843bd783.png)

## 테이블 설계
![테이블설계](https://user-images.githubusercontent.com/52727315/156873394-1ccb1ab4-97de-4b02-826d-29830d2f60f8.png)

## 도메인 설계
![도메인설계](https://user-images.githubusercontent.com/52727315/156873388-21d6270d-074f-4e73-8e00-185919876340.png)


## 엔티티 설계시 주의점

- Setter 함수는 지양
  - 변경 포인트가 많아진다
  - 유지보수가 어렵다

- 모든 연관관계는 지연로딩(LAZY)으로 설정한다
  - 즉시로딩(EAGER)은 예측이 어렵고 어떤 쿼리가 실행될 지 추적이 어렵다
  - **🔥JPQL에서 N+1 문제가 자주 발생한다(1개의 쿼리를 실행했는데 데이터의 N개 개수만큼 쿼리가 실행되는 문제. 성능에 너무 안좋음**

- 컬렉션은 필드에서 초기화하자
  - Null문제에 안전하다
  - 엔티티를 영속화 할 때 하이버네이트가 내장 컬렉션으로 변경한다 이 과정에서 잘못 생성하는 문제가 발생할 수 있다

## ...ToOne 관계 예제(주의사항)
- ToMany관계에선 지연로딩이 Default로 설정되어 있지만 ToOne관계에선 즉시로딩이 Default로 설정되어 있다.  
때문에 ToOne 관계에선 아래와 같이 ```fetch = FetchType.LAZY```를 꼭 추가해주도록 하자
```java
  ...
  @ManyToOne(fetch = FetchType.LAZY)
  private Order order;
  ...
```

## 연관관계의 주인(mappedBy)

- 양방향 연관관계를 사용해야 할 경우 mappedBy를 통하여 누가 주인이 될지 지정해줘야한다  
보통 Fk를 갖고있는 쪽을 주인으로 지정하면 된다.
```java
@Entity
public class Member {
  ...
  @OneToMany(mappedBy = "member")
  private List<Order> orders = new ArrayList<>();
  ...
```

```java
@Entity
private class Order{
  ...
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id")
  private Member member;
  ...
```



## 값 타입 예제

- Address 클래스를 값타입으로 사용하고 싶을 땐 @Embeddable을 선언하여 값타입이라는것을 지정해준다.
```java
@Embeddable
@Getter
public class Address {
  ...
```
- 사용하는 엔티티에선 @Embedded을 사용한다
```java
@Entity
public class Delivery {
  ...
  
  @Embedded
  private Address address;
```

## Enum 사용 예제

- 아래와 같은 Enum 클래스를 생성해준다
```java
public enum DeliveryStatus {
  READY, COMP
  ...
```

- @Enumerated을 사용하는데 꼭 ```(EnumType.STRING)```을 넣어줘야한다.  
```(EnumType.ORIGINAL)```이 Default로 지정되어 있고 이건 순서로 값이 들어가기 때문에 굉장히 위험하다
```java
  ...
  @Enumerated(EnumType.STRING)
  private DeliveryStatus status;
  ...
```

# ✔활용 2편 내용











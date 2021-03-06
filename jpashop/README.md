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

## Entity는 외부로 노출시키지 않는다

- 프레젠테이션 계층을 위한 로직이 추가된다
- API검증을 위한 로직이 들어가게 된다
- 각각의 API를 위한 요구사항을 담기 어렵다
- 🔥엔티티가 변경될 경우 API 스펙이 변경되게 된다🔥
 


## 조회 방식 권장 순서
- 첫번째. 엔티티 -> DTO로 변환
- 두번째. 페치 조인으로 성능 최적화(대부분의 이슈가 해결된다)
- 세번째. 그래도 안되면 DTO로 조회
- 네번째. 모든방법이 다 안되면 JPA의 네이티브SQL 혹은 JDBC Template사용
 

## Entity를 DTO로 변환 예제
```java
public List<Order> findAllWithMemberDelivery() {
  return em.createQuery(
    "select o from Order o" +
    " join fetch o.member m" +
    " join fetch o.delivery d", Order.class)
  .getResultList();
}
```

```java
// 엔티티로 조회한 후 stream의 map을 사용하여 간단하게 Dto로 변환
public List<SimpleOrderDto> ordersV3() {
  List<Order> orders = orderRepository.findAllWithMemberDelivery();
  List<SimpleOrderDto> result = orders.stream()
                                      .map(o -> new SimpleOrderDto(o))
                                      .collect(toList());
  return result;
}
```

## DTO조회 예제
```java
// DTO로 바로 조회할 경우 패키지 경로를 Full로 적어줘야한다
public List<OrderSimpleQueryDto> findOrderDtos() {
	return em.createQuery(
  "select new jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDto(o.id, m.name, o.orderDate, o.status, d.address)" +
    " from Order o" +
    " join o.member m" +
    " join o.delivery d", OrderSimpleQueryDto.class)
	.getResultList();
 }
```

## 페치 조인의 한계
- 컬렉션을 페치조인하면 페이징이 불가능하다
  - 1:N 관계에서 N을 기준으로 row가 생성되기 때문
- 컬렉션에서 페치조인 사용시 메모리에서 페이징을 시도 -> 장애로 이어진다

## 페치 조인 한계 돌파
- 첫번째. ToOne 연관관계를 모두 페치조인하여 조회한다
- 두번째. 컬렉션들은 지연로딩을 사용하여 조회한다
- 추가적으로 지연로등 성능 최적화를 위해 최적화 설정을 적용한다
  - 배치사이즈 설정을 하면 지연로딩을 사용하여 처리를 해야했던 N+1 문제가 1+1로 최적화된다 
  - hibernate.default_batch_fetch_size: 글로벌 설정
  - @BatchSize : 개별 최적화
```java
// application.yml에 글로벌 설정 
spring:
  jpa:
    properties:
      hibernate:
        default_batch_fetch_size: 500 // 사이즈는 100~1000 사이로 성능테스트 후 최적의 설정값을 찾는다
```

## 페치 조인을 사용한 페이징 처리 예제
```java
// ToOne 연관관계는 페이징 문제가 되지 않기 때문에 페치조인을 한다
public List<Order> findAllWithMemberDelivery(int offset, int limit) {
	return em.createQuery(
	"select o from Order o" +
	" join fetch o.member m" +
	" join fetch o.delivery d", Order.class)
	.setFirstResult(offset)
	.setMaxResults(limit)
	.getResultList();
}
```
```java
public List<OrderDto> ordersV3_page(@RequestParam(value = "offset",defaultValue = "0") int offset, @RequestParam(value = "limit", defaultValue = "100") int limit) {
	List<Order> orders = orderRepository.findAllWithMemberDelivery(offset,limit);
	List<OrderDto> result = orders.stream()
                                      .map(o -> new OrderDto(o))
                                      .collect(toList());
	return result;
}


// 예제를 위하여 Inner Class로 선언
@Data
static class OrderDto {
	private Long orderId;
	private String name;
	private LocalDateTime orderDate;
	private OrderStatus orderStatus;
	private Address address;
	private List<OrderItemDto> orderItems;

	public OrderDto(Order order) {
	    orderId = order.getId();
	    name = order.getMember().getName();
	    orderDate = order.getOrderDate();
	    orderStatus = order.getStatus();
	    address = order.getDelivery().getAddress();
	    orderItems = order.getOrderItems().stream().map(OrderItemDto::new).collect(Collectors.toList());
	}
}
```

## 조회 시 참고사항
- 위의 예제는 Entity를 사용하여 조회하지만 이외에 DTO를 사용하여 조회하는 방법도 있다.  
DTO를 사용하여 조회할 경우 성능최적화를 해야할 때 많은 코드를 변경해야한다.  
허나 성능최적화는 단순한 코드를 복잡한 코드로 몰고가듯 DTO 방식은 SQL을 직접 다루는것과 유사하여 둘 사이의 줄타기가 필요하다

## OSIV성능 촤적화
- 상황에 따라 실시간 트래픽이 많은 API의 경우 OSIV를 끄고, ADMIN처럼 커넥션을 많이 사용하지 않으면 OSIV를 킨다
- Open Sesison in View : 하이버네이터
- Open EntityManager In View : JPA
- 관례상 OSIV
```java
// application.yml
spring.jpa.open-in-view : true(defalut)/false
```

![OSIV ON](https://user-images.githubusercontent.com/52727315/156882628-3c482364-e996-4ffb-a092-7fa6830abcf5.png)

- OSIV 전략을 사용하면 DB커넥션 시작 시점부터 API 응답이 끝날 때 까지 영속성 컨텍스트와 DB커넥션을 유지한다

![OSIV OFF](https://user-images.githubusercontent.com/52727315/156882625-b6cf1e6b-56b1-4159-8b1b-aecc65d63cf6.png)

- OSIV를 끄면 트랜잭션 종료 시 영속성 컨텍스트가 닫히고, DB커넥션도 반환하게 된다.






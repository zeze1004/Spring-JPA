# 개발 정리 노트



### 엔티티 설계 주의점

1. 가급적 `Setter` 사용x

   - 변경 포인터가 너무 많아져서 유지보수가 어려워짐

2. **모든 연관관계는 지연로딩으로 설정**

   - 실무에서는 모든 연관관계를 `LAZY`(지연로딩)로 설정해야함!!!!⭐⭐⭐ <- 매우 중요

     =>  `EAGER`(즉시로딩)은 예측이 어려워 어떤 SQL이 실행될지 알 수 X

     - JPQL에서 `EAGER` 사용 시✨ `N+1` 문제✨ 자주 발생 

     - 충격실화 ㄴㅇㄱ

       `@OneToOne`, `@ManyToOne`은 모두 즉시로딩이 디폴트 값

       => xxxToOne은 꼭꼭 지연로딩 해줘야 함

       **`LAZY` 설정 예시**

       ```java
       // 여러 회원들이 하나의 주문에 접근
           @ManyToOne(fetch = FetchType.LAZY)
           @JoinColumn(name = "member_id")
           private Member member;
       ```

       - 반면, xxxToMany는 디폴트 값이 지연로딩이어서 따로 지정 안 해도 괜찮음 

   - 즉시로딩을 하면 연관된 엔티티 다 딸려오므로, DB에서 함께 조인하고 싶을 때

   1. `fetch join` 사용

   2. 엔티티 그래프 기능 사용

3. 컬렉션은 필드에서 초기화 해야함

   하이버네이트가 엔티티 영속화 후 h2 내장 컬렉션으로 엔티티 변경해서 생각하는데로 값이 저장되지 않을 수 있음!

   => 필드에 저장하면 이런 문제에서 안전함

   => 웬만하면 컬렉션 필드로 선언하고 수정하지 말자!

   ```java
   	// 하나의 회원이 여러 주문을 가짐
       @OneToMany(mappedBy = "member")     
   	// order 테이블에 속함, order에서 member 바꾸면 바뀌지 member 테이블에서 member 바꿀 수 x
       private List<Order> orders = new ArrayList<>(); // 컬렉션 선언하고 수정x
   ```



4. 연관관계 메서드

   **추가 설명 삽입 요망!**

   **`Order` 연관관계 메서드 코드**

   ```java
      // 연관관계 메서드
       // Order랑 연관관계인 3가지 엔티티들을 외래 키와 매핑
       public void setMember(Member member) {
           this.member = member;
           member.getOrders().add(this);
       }
   
       public void addOrderItem(OrderItem orderItem) {
           orderItems.add(orderItem);
           orderItem.setOrder(this);
       }
   
       public void setDelivery(Delivery delivery) {
           this.delivery = delivery;
           delivery.setOrder(this);
       }
   ```

   

> `Cascade` 사용법

​	`Order` 하나에 `OrderItem` 은 여러 개 매핑 가능

​	`Cascade`를 하면 `Order`에 연관된 `OrderItem`을 한 번에 매핑할 수 있고 `Order` 삭제 시 한 번에 여러 `OrderItem` 삭제 가능

	##### Cascade 예제

```java
@OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
private List<OrderItem> orderItems = new ArrayList<>();
```



> ##### 도메인 모델 패턴
>
> 엔티티가 비즈니스 로직을 가지는 모델
>
> - 데이터를 가지고 있는 쪽이 비즈니스 로직을 가지고 있어야 응집력이 있음
>
> - 서비스 계층은 단순히 엔티티에 필요한 요청을 위임하는 역할

> ##### 트랜잭션 스크립트 패턴
>
> 도메인 모델 패턴과 반대로 서비스 계층에서 비즈니스 로직을 대부분 처리하는 패턴
>
> - 엔티티에는 비즈니스 로직이 거의 없음



### 주문 도메인 개발




# 개발 정리 노트


##### 홈 화면
![home_view](https://user-images.githubusercontent.com/44468282/112165188-dcc33980-8c31-11eb-8f05-d625dad928a7.png)



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

##### 테스트코드 템플릿

```java
    @Test
    public void 테스트이름() throws Exception {
        // given

        // when

        // then
    }
```



> ##### 테스트 코드 오류
>
> `No runnalble methods`
>
> `import org.junit.Test;` 로 import 되었는지 꼭 확인!



### 주문 검색 개발

- JPA에서 동적 쿼리 사용해보자



```java
// OrderRepository
	public List<Order> findAll(OrderSearch orderSearch) {
        // 주문 검색하고 주문과 멤버와 join
        return em.createQuery("select o from Order o join o.member m" +
                " where o.status = : status" +
                " and m.name like :name", Order.class)
                // 바인딩
                .setParameter("status", orderSearch.getOrderStatus())
                .setParameter("name", orderSearch.getMemberName())
                .setMaxResults(1000) // 결과 최대 1000개까지 보여주기
                .getResultList();
```

위처럼 작성하면 Order가 null일 경우 Order.status가 없으므로 제대로 동작 되지 x



##### 어떻게 해결할까😥

1. JPA 쿼리로 해결

   ```java
       public List<Order> findAllByString(OrderSearch orderSearch) {
           // language=JPAQL
           String jpql = "select o From Order o join o.member m";
           boolean isFirstCondition = true;
           //주문 상태 검색
           if (orderSearch.getOrderStatus() != null) {
               if (isFirstCondition) {
                   jpql += " where";
                   isFirstCondition = false;
               } else {
                   jpql += " and";
               }
               jpql += " o.status = :status";
           }
           //회원 이름 검색
           if (StringUtils.hasText(orderSearch.getMemberName())) {
               if (isFirstCondition) {
                   jpql += " where";
                   isFirstCondition = false;
               } else {
                   jpql += " and";
               }
               jpql += " m.name like :name";
           }
           TypedQuery<Order> query = em.createQuery(jpql, Order.class)
                   .setMaxResults(1000); //최대 1000건
           if (orderSearch.getOrderStatus() != null) {
               query = query.setParameter("status", orderSearch.getOrderStatus());
           }
           if (StringUtils.hasText(orderSearch.getMemberName())) {
               query = query.setParameter("name", orderSearch.getMemberName());
           }
           return query.getResultList();
       }
   ```

   => 💖이렇게 쓰지말자💖

2. **JPA Criteria**: JPA가 제공해주는 동적쿼리 빌드해주는 기능

   ```java
   // JPA Criteria: JPA가 제공해주는 동적쿼리 빌드해주는 기능
       public List<Order> findAllByCriteria(OrderSearch orderSearch) {
           CriteriaBuilder cb = em.getCriteriaBuilder();
           CriteriaQuery<Order> cq = cb.createQuery(Order.class);
           Root<Order> o = cq.from(Order.class);
           Join<Order, Member> m = o.join("member", JoinType.INNER); //회원과 조인
           List<Predicate> criteria = new ArrayList<>();
           //주문 상태 검색
           if (orderSearch.getOrderStatus() != null) {
               Predicate status = cb.equal(o.get("status"),
                       orderSearch.getOrderStatus());
               criteria.add(status);
           }
           //회원 이름 검색
           if (StringUtils.hasText(orderSearch.getMemberName())) {
               Predicate name =
                       cb.like(m.<String>get("name"), "%" +
                               orderSearch.getMemberName() + "%");
               criteria.add(name);
           }
           cq.where(cb.and(criteria.toArray(new Predicate[criteria.size()])));
           TypedQuery<Order> query = em.createQuery(cq).setMaxResults(1000); //최대 1000개
           return query.getResultList();
       }
   ```

   - JPA Criteria는 코드를 한 눈에 이해하기 힘들어서 유지보수하기 힘듦

      => 동적 쿼리는 무족권 🍓`Querydsl`🍓를 사용함

     ​		(추후 Querydsl 코드 추가 필요)





### 화면 만들기

- 타임리프, 부트스트랩 사용



### 회원가입

### ![MEMBER_SIGNUP](C:\Project\Spring-JPA\images\MEMBER_SIGNUP.png)

- 이름 작성을 필수로 받을거임

- 작성 안 했을 시 submit 버튼 작동 안 하게 구현

  - `BindingResult result`

    : 에러 발생해도 에러 결과를 가진 채 코드 실행

    - 기존에 작성된 이름 외의 form에 작성된 text들은 `MemberForm form`에 담겨있어 값이 바뀌지 않음

  ```java
      @PostMapping("/members/new")
      public String create(@Valid MemberForm form, BindingResult result) {    // 에러가 있어도 BindingResult가 에러 결과를 갖고 아래 코드 동작
  
          // 에러 발생 시 페이지 넘어가지 않게 하기
          if (result.hasErrors()) {
              return "members/createMemberForm";
          }
  
          Address address = new Address(form.getCity(), form.getStreet(), form.getZipcode());
  
          Member member = new Member();
          member.setName(form.getName());
          member.setAddress(address);
  
          memberService.join(member);
          return "redirect:/"; // 회원가입 폼 작성 완료시 홈으로 이동
      }
  ```

  



### 회원 목록 조회



![MEMBER_LIST](C:\Project\Spring-JPA\images\MEMBER_LIST.png)

- 홈 화면의 회원 목록 버튼 누르면 `memberList.html` 띄워줌

  ```java
  @GetMapping("/members")
      public String list(Model model) {   // Model이란 객체를 통해 화면에 데이터 전달
          
          // model.addAttribute("members",memberService.findMember()); 아래 두 코드를 합친 것
          List<Member> members = memberService.findMember();
          model.addAttribute("members", members);
          return "members/memberList";
      }
  ```

  - `ctrl + alt + n`: 여러 줄에 중복되는 변수를 짧게 줄일 수 있음





### 상품 등록

- 회원가입과 유사



### 상품 목록

- 회원목록과 유사



### 상품 수정⭐⭐⭐

- 매우 매우 중요!!!





### 변경 감지와 병합(merge)



- **준영속 엔티티란?**

  영속성 컨텍스트가 더는 관리하지 않는 엔티티를 뜻함

  > DB에 한 번 저장되면 객차는 식별자가 생긴다
>
  > **상품 수정**에서 Book 객체는 이미 DB에 저장되어 있어서 식별자가 존재
>
  > 그러나 JPA에가 더는 관리하지 X

- 반면, JPA가 관리하는 영속상태의 엔티티는 변경감지가 일어남

  트랜잭션 발생 시 변경 감지 기능(**Dirty Checking**)

  

##### 그렇다면 준영속 엔티티를 어떻게 수정해야 할까?

- 준영속 엔티티를 수정하는 2가지 방법

  1. 변경 감지 기능(Dirty Checking) 사용

     ```java
     // ItemService    
     	@Transactional
         public void updateItem(Long itemId, Book Param) {
             Item findItem = itemRepository.findOne(itemId);
             findItem.setPrice(Param.getPrice());
             findItem.setName(Param.getName());
             findItem.setStockQuantity(Param.getStockQuantity());
         }
     ```

     - 영속성 컨텍스트로 엔티티를 다시 불러 들여 조회

     - 트랜잭션 안에서 엔티티 조회하고 변경할 값을 선택하므로

       트랜잭션 커밋 시점에서 변경 감지

     - 바뀐 값으로 DB에 업데이트

  2. 병합(merge) 사용

     병합은 준영속 상태의 엔티티를 영속 상태를 영속 상태로 변경할 때 사용하는 기능

     ```java
     	@Transactional
     	void update(Item itemParam) { 
             //itemParam: 파리미터로 넘어온 준영속 상태의 엔티티
     		Item mergeItem = em.merge(item);
     	}
     ```

     1.의 코드와 같은 코드

     병합이 자동으로 넘어온 매개변수와 같은 **엔티티를 찾아서** 영속 상태로 변경한 후

     변경된 값 DB로 업데이트

     

     ​	**엔티티 찾는 과정**

     ​	1) 엔티티를 찾을 때 먼저 영속성 컨텍스트(1차 캐시)에서 찾고

     ​		없을 시 DB에서 찾음

     ​	2) 찾은 엔티티를 준영속상태에서 영속 상태로 변경

     > **병합 사용시 주의점**
     >
     > 병합은 엔티티의 모든 속성이 변경됨
     >
     > 변경될 값이 없으면 null로 업데이트 할 수도 있음
     >
     > 반면, 변경 감지 기능은 원하는 속성만 선택해서 업데이트 가능

  

  #### 그래서 엔티티 변경할 때는 어떻게 해야할까?

  - merge는 최대한 쓰지 말자!
  - 변경 감지 기능을 사용할 것!

  

  
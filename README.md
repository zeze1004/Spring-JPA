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

   














# API 개발 기본



- API에서는 절대 엔티티를 파라미터로 받아오지 말 것!
  - 엔티티 변경시 큰 장애 발생
  - 엔티티 노출 가능성 있음

- 커맨드와 쿼리는 철저히 분리하자!



### 회원조회

```java
    // 안좋은 버전의 조회
    @GetMapping("api/v1/members")
    public List<Member> membersV1() {
        return memberService.findMember();
    }
```

- api에서 엔티티를 직접 반환하면 큰일남!

  - member 안에 있는 order 값도 반환됨

    ```java
    public class Member {
        @Id @GeneratedValue
        @Column(name = "member_id")
        private Long id;
        private String name;
        @Embedded
        private Address address;
        // 하나의 회원이 여러 주문을 가짐
        @OneToMany(mappedBy = "member") 
        @JsonIgnore // ✨
        private List<Order> orders = new ArrayList<>();
    }
    ```

    - `@JsonIgnore`:

      스프링은 Json으로 엔티티 전달하므로 이 어노테이션 쓰면 order는 전달되지 않을 수 있지만 

      order가 필요한 다른 api는 어쩔거임?! 💥

**좋은 수정 API**

```java
   // 조회
    @GetMapping("api/v2/members")
    public Result membersV2() {
        List<Member> findMembers = memberService.findMember();
        List<MemberDto> collect = findMembers.stream()
                .map(m -> new MemberDto(m.getName()))
                .collect(Collectors.toList());
        return new Result(collect);
    }

    @Data
    @AllArgsConstructor
    // 컬렉션을 직접 반환하면 API 스펙 변경하기 어려우므로
    // 별도의 Result 클래스를 생성하자
    static class Result<T> {
        private T data;
    }

    @Data
    @AllArgsConstructor
    static class MemberDto {
        private String name;
    }

```

- API에 맞추어 별도의 DTO를 반환하자!
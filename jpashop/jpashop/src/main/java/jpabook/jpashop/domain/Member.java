package jpabook.jpashop.domain;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;
@Entity
@Getter
@Setter
public class Member {
    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    private String name;
    @Embedded
    private Address address;
    
    // 하나의 회원이 여러 주문을 가짐
    @OneToMany(mappedBy = "member")     // order 테이블에 속함, order에서 member 바꾸면 바뀌지 member 테이블에서 member 바꿀 수 x
    private List<Order> orders = new ArrayList<>();
}
package jpabook.jpashop.domain.category;

import jpabook.jpashop.domain.item.Item;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Category {
    @Id @GeneratedValue
    @Column(name = "category_id")
    private Long id;
    private String name;

    // category와 item은 다대다 관계
    @ManyToMany
    @JoinTable(name = "category_item", // 다대다 관계는 각각의 기본키를 갖는 중간테이블이 필요함
            // 실무에서 다대다를 쓰지 못하는 이유는 중간테이블 정해지면 더 추가하기 힘들어서임
            joinColumns = @JoinColumn(name = "category_id"),
            inverseJoinColumns = @JoinColumn(name = "item_id"))
    private List<Item> items = new ArrayList<>();
    
    // 부모-자식 연관관계 만드는 법
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    @OneToMany(mappedBy = "parent")
    private List<Category> child = new ArrayList<>();
}

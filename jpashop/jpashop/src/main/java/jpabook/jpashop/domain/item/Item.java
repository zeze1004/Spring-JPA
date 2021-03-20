package jpabook.jpashop.domain.item;

import jpabook.jpashop.domain.category.Category;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)   // 싱글테이블 전략: item_id에 Album, Book, Movie 변수 다 저장
@DiscriminatorColumn(name = "dtype")
@Getter @Setter
// 추상클래스: Album, Book, Movie가 Item 상속 받음
public abstract class Item {
    @Id
    @GeneratedValue
    @Column(name = "item_id")
    private Long id;

    private String name;
    private int price;
    private int stockQuantity;

    @ManyToMany(mappedBy = "items")
    private List<Category> categories = new ArrayList<>();
}

package jpabook.jpashop.domain.item;

import jpabook.jpashop.domain.category.Category;
import jpabook.jpashop.domain.exception.NotEnoughStockException;
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

    // 비즈니스 로직: 데이터를 가지고 있는 엔티티에 비즈니스 로직 가지고 있어야 응집력이 있음

    // 재고(stock) 수량을 증가시키는 로직
    public void addStock(int quantity) {
        this.stockQuantity += quantity;
    }

    // 재고 줄이는 로직
    public void removeStock(int quantity) {
        int restStock = this.stockQuantity - quantity;
        // 0보다 크면 예외처리 발생 시키는 로직
        if (restStock < 0) {
            throw new NotEnoughStockException("need more stock");
        }
        this.stockQuantity = restStock;
    }
}

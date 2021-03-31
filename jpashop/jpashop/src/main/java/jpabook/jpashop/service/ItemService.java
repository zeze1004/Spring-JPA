package jpabook.jpashop.service;

import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
// ItemRepository 위임받은 코드
public class ItemService {
    private final ItemRepository itemRepository;

    // item 저장
    @Transactional  // 메소드 위에 오버라이딩한게 우선권을 지님
                    // 저장은 readOnly 적용 x
    public void saveItem(Item item) {
        itemRepository.save(item);
    }

    @Transactional
    public void updateItem(Long itemId, String name, int price, int stockQuantity) {   // Param = 파라미터 줄인 말
        Item findItem = itemRepository.findOne(itemId);
        findItem.setPrice(price);
        findItem.setName(name);
        findItem.setStockQuantity(stockQuantity);
    }

    // 조회 => @Transactional(readOnly = true) 적용
    public List<Item> findItems() {
        return itemRepository.findAll();
    }

    // 찾기
    public Item findOne(Long itemId) {
        return itemRepository.findOne(itemId);
    }

}

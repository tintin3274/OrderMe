package th.ku.orderme.service;

import org.springframework.stereotype.Service;
import th.ku.orderme.model.Item;
import th.ku.orderme.repository.ItemRepository;

import java.util.List;

@Service
public class ItemService {
    private final ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public List<Item> findDrink(){
        return itemRepository.findItemByCategoryEquals("DRINK");
    }
}

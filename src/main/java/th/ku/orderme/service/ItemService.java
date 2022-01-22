package th.ku.orderme.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import th.ku.orderme.model.Item;
import th.ku.orderme.model.Optional;
import th.ku.orderme.repository.ItemRepository;

import java.util.*;

@Service
@AllArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;

    public Item findById(int id) {
        return itemRepository.findById(id).orElse(null);
    }

    public Item addItem(Item item) {
        return item;
    }

    public Map<Integer,Item> getMapAllItemOfItemId(int itemId) {
        Map<Integer,Item> itemMap = new HashMap<>();
        Item item = findById(itemId);
        if(item != null) {
            itemMap.put(item.getId(), item);
            for(Optional optional : item.getOptionalList()) {
                for(Item itemOption : optional.getItemList()) {
                    itemMap.put(itemOption.getId(), itemOption);
                }
            }
        }
        return itemMap;
    }

    public Map<Integer,Set<Integer>> getMapSetItemIdOfOptionalIdOfItemId(int itemId) {
        Map<Integer,Set<Integer>> map = new HashMap<>();
        Item item = findById(itemId);
        if(item != null) {
            for(Optional optional : item.getOptionalList()) {
                Set<Integer> set = new HashSet<>();
                for(Item itemOption : optional.getItemList()) {
                    set.add(itemOption.getId());
                }
                map.put(optional.getId(), set);
            }
        }
        return map;
    }

    public Item saveAndFlush(Item item) {
        return itemRepository.saveAndFlush(item);
    }

    public List<Item> findAllFood() {
        return itemRepository.findItemByCategoryNot("OPTION");
    }
}

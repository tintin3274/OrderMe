package th.ku.orderme.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import th.ku.orderme.model.Item;
import th.ku.orderme.model.ItemOptional;
import th.ku.orderme.model.Optional;
import th.ku.orderme.repository.ItemOptionalRepository;
import th.ku.orderme.repository.ItemRepository;
import th.ku.orderme.repository.OptionalRepository;
import th.ku.orderme.util.ConstantUtil;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final ItemOptionalRepository itemOptionalRepository;
    private final OptionalRepository optionalRepository;

    public Item findById(int id) {
        return itemRepository.findById(id).orElse(null);
    }

    public Item addItem(Item item, List<Integer> optionGroupId) {
        try {
            if(optionGroupId == null) optionGroupId = new ArrayList<>();
            for(int id : optionGroupId) {
                if(!optionalRepository.existsById(id)) throw new IllegalArgumentException("Invalid Optional ID: "+id);
            }

            item = itemRepository.saveAndFlush(item);

            List<ItemOptional> itemOptionalList = new ArrayList<>();
            for(int i=0; i<optionGroupId.size(); i++) {
                ItemOptional.ItemOptionalId itemOptionalId = new ItemOptional.ItemOptionalId();
                itemOptionalId.setItemId(item.getId());
                itemOptionalId.setOptionalId(optionGroupId.get(i));

                ItemOptional itemOptional = new ItemOptional();
                itemOptional.setItemOptionalId(itemOptionalId);
                itemOptional.setNumber(i+1);

                itemOptionalList.add(itemOptional);
            }
            itemOptionalRepository.saveAllAndFlush(itemOptionalList);
            return item;
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage());
            return null;
        }
    }

    public Map<Integer,Item> getMapAllItemOfItemId(int itemId) {
        Map<Integer,Item> itemMap = new HashMap<>();
        Item item = findById(itemId);
        if(item != null && item.getFlag() == ConstantUtil.FLAG_NORMAL) {
            itemMap.put(item.getId(), item);
            for(Optional optional : item.getOptionalList()) {
                if(optional.getFlag() == ConstantUtil.FLAG_NORMAL) {
                    for(Item itemOption : optional.getItemList()) {
                        if(itemOption.getFlag() == ConstantUtil.FLAG_NORMAL) {
                            itemMap.put(itemOption.getId(), itemOption);
                        }
                    }
                }
            }
        }
        return itemMap;
    }

    public Map<Integer,Set<Integer>> getMapSetItemIdOfOptionalIdOfItemId(int itemId) {
        Map<Integer,Set<Integer>> map = new HashMap<>();
        Item item = findById(itemId);
        if(item != null && item.getFlag() == ConstantUtil.FLAG_NORMAL) {
            for(Optional optional : item.getOptionalList()) {
                if(optional.getFlag() == ConstantUtil.FLAG_NORMAL) {
                    Set<Integer> set = new HashSet<>();
                    for(Item itemOption : optional.getItemList()) {
                        if(itemOption.getFlag() == ConstantUtil.FLAG_NORMAL) {
                            set.add(itemOption.getId());
                        }
                    }
                    map.put(optional.getId(), set);
                }
            }
        }
        return map;
    }

    public List<Item> findAllFood() {
        return itemRepository.findItemByCategoryNotAndFlagEquals("OPTION", ConstantUtil.FLAG_NORMAL);
    }

    public List<String> findAllFoodCategory() {
        return itemRepository.findAllFoodCategory();
    }

    public List<Item> findItemByCategory(String category) {
        return itemRepository.findItemByCategoryEqualsAndFlagEquals(category, ConstantUtil.FLAG_NORMAL);
    }
}

package th.ku.orderme.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import th.ku.orderme.model.Item;
import th.ku.orderme.model.ItemOptional;
import th.ku.orderme.model.Optional;
import th.ku.orderme.repository.ItemOptionalRepository;
import th.ku.orderme.repository.ItemRepository;
import th.ku.orderme.repository.OptionalItemRepository;
import th.ku.orderme.repository.OptionalRepository;
import th.ku.orderme.util.ConstantUtil;

import javax.transaction.Transactional;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final ItemOptionalRepository itemOptionalRepository;
    private final OptionalRepository optionalRepository;
    private final OptionalItemRepository optionalItemRepository;

    public Item findById(int id) {
        return itemRepository.findById(id).orElse(null);
    }

    public List<Item> findAll() {
        return itemRepository.findAllByFlagEquals(ConstantUtil.FLAG_NORMAL);
    }

    @Transactional
    public Item addItem(Item item, List<Integer> optionGroupId) {
        if(optionGroupId == null) optionGroupId = new ArrayList<>();
        if(!validateOptionGroupId(optionGroupId)) return null;
        item = itemRepository.saveAndFlush(item);
        itemOptionalRepository.deleteItemOptionalByItemOptionalId_ItemId(item.getId());

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
    }

    @Transactional
    public Item updateItem(Item item) {
        Item oldItem = itemRepository.findById(item.getId()).orElse(null);
        if(oldItem == null || oldItem.getFlag() != ConstantUtil.FLAG_NORMAL) return null;
        item.setVersion(oldItem.getVersion());
        return itemRepository.saveAndFlush(item);
    }

    @Transactional
    public Item updateItemOptional(int id, List<Integer> optionGroupId) {
        Item item = itemRepository.findById(id).orElse(null);
        if(item == null || item.getFlag() != ConstantUtil.FLAG_NORMAL) return null;
        return addItem(item, optionGroupId);
    }

    @Transactional
    public Item deleteItem(int id) {
        Item item = findById(id);
        if(item == null) return null;
        item.setFlag(ConstantUtil.FLAG_DELETE);
        item = itemRepository.saveAndFlush(item);
        if(item.getCategory().equalsIgnoreCase(ConstantUtil.OPTION)) {
            optionalItemRepository.deleteOptionalItemByOptionalItemId_ItemId(id);
        }
        else {
            itemOptionalRepository.deleteItemOptionalByItemOptionalId_ItemId(id);
        }
        return item;
    }

    private boolean validateOptionGroupId(List<Integer> optionGroupId) {
        try {
            List<Optional> optionalList = optionalRepository.findAllById(optionGroupId);
            for(Optional optional : optionalList) {
                if(optional.getFlag() != ConstantUtil.FLAG_NORMAL)
                    throw new IllegalArgumentException("Invalid Optional ID: "+optional.getId());
            }
            return true;
        }
        catch (IllegalArgumentException e) {
            log.error(e.getMessage());
        }
        return false;
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

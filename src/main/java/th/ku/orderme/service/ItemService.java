package th.ku.orderme.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import th.ku.orderme.model.IndexCategory;
import th.ku.orderme.model.Item;
import th.ku.orderme.model.ItemOptional;
import th.ku.orderme.model.Optional;
import th.ku.orderme.repository.*;
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
    private final IndexCategoryRepository indexCategoryRepository;

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

        addCategory(item.getCategory());
        return item;
    }

    @Transactional
    public Item updateItem(Item item) {
        Item oldItem = itemRepository.findById(item.getId()).orElse(null);
        if(oldItem == null || oldItem.getFlag() != ConstantUtil.FLAG_NORMAL) return null;

        boolean updateCategory = false;

        if(!oldItem.getCategory().equalsIgnoreCase(item.getCategory())) {
            oldItem.setCategory(item.getCategory());
            addCategory(item.getCategory());
            updateCategory = true;
        }

        oldItem.setName(item.getName());
        oldItem.setDescription(item.getDescription());
        oldItem.setPrice(item.getPrice());
        oldItem.setQuantity(item.getQuantity());
        oldItem.setCheckQuantity(item.isCheckQuantity());
        oldItem.setDisplay(item.isDisplay());
        item = itemRepository.saveAndFlush(oldItem);

        if(updateCategory) updateCategory();
        return item;
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

    public List<String> getAllCategory() {
        return indexCategoryRepository.getAllCategory();
    }

    public void addCategory(String category) {
        if(!category.equalsIgnoreCase(ConstantUtil.OPTION)) {
            if(!indexCategoryRepository.existsById(category)) {
                IndexCategory indexCategory = new IndexCategory(category, 0);
                indexCategoryRepository.saveAndFlush(indexCategory);
            }
        }
    }

    @Transactional
    public boolean changeCategory(String oldCategory, String newCategory) {
        if(!oldCategory.equalsIgnoreCase(ConstantUtil.OPTION) && !newCategory.equalsIgnoreCase(ConstantUtil.OPTION)) {
            IndexCategory oldIndexCategory = indexCategoryRepository.findById(oldCategory).orElse(null);
            if(oldIndexCategory != null) {
                IndexCategory newIndexCategory = new IndexCategory(newCategory, oldIndexCategory.getNumber());
                indexCategoryRepository.saveAndFlush(newIndexCategory);
                indexCategoryRepository.delete(oldIndexCategory);

                List<Item> itemList = itemRepository.findItemByCategoryEqualsAndFlagEquals(oldCategory, ConstantUtil.FLAG_NORMAL);
                for(Item item : itemList) {
                    item.setCategory(newCategory);
                }
                itemRepository.saveAllAndFlush(itemList);
                return true;
            }
        }
        return false;
    }

    public void updateCategory() {
        List<String> categoryList = itemRepository.findAllFoodCategory();
        List<IndexCategory> indexCategoryList = indexCategoryRepository.findAllById(categoryList);
        if(categoryList.size() > indexCategoryList.size()) {
            List<String> allCategory = getAllCategory();
            List<IndexCategory> newIndexCategoryList = new ArrayList<>();
            for(String category : categoryList) {
                if(!allCategory.contains(category)) {
                    newIndexCategoryList.add(new IndexCategory(category, 0));
                }
            }
            indexCategoryRepository.deleteAll();
            indexCategoryRepository.saveAllAndFlush(indexCategoryList);
            indexCategoryRepository.saveAllAndFlush(newIndexCategoryList);
        }
        else {
            indexCategoryRepository.deleteAll();
            indexCategoryRepository.saveAllAndFlush(indexCategoryList);
        }
    }

    public void addCategorySortList(List<String> categoryList) {
        List<IndexCategory> indexCategoryList = new ArrayList<>();
        for(int i=0; i<categoryList.size(); i++) {
            indexCategoryList.add(new IndexCategory(categoryList.get(i), i+1));
        }
        indexCategoryRepository.saveAllAndFlush(indexCategoryList);
    }

    public void deleteCategory(String category) {
        if(indexCategoryRepository.existsById(category)) {
            indexCategoryRepository.deleteById(category);
        }
    }
}

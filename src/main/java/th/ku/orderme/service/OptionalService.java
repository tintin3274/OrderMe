package th.ku.orderme.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import th.ku.orderme.model.Item;
import th.ku.orderme.model.Optional;
import th.ku.orderme.model.OptionalItem;
import th.ku.orderme.repository.ItemRepository;
import th.ku.orderme.repository.OptionalItemRepository;
import th.ku.orderme.repository.OptionalRepository;
import th.ku.orderme.util.ConstantUtil;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.*;

@Slf4j
@Service
@AllArgsConstructor
public class OptionalService {
    @PersistenceContext
    private final EntityManager entityManager;
    private final OptionalRepository optionalRepository;
    private final OptionalItemRepository optionalItemRepository;
    private final ItemRepository itemRepository;

    public Optional findById(int id) {
        return optionalRepository.findById(id).orElse(null);
    }

    public List<Integer> getAllItemIdOfOptionalId(int optionalId) {
        List<Integer> itemIdList = new ArrayList<>();
        Optional optional = findById(optionalId);
        if(optional != null && optional.getFlag() == ConstantUtil.FLAG_NORMAL) {
            for(Item item : optional.getItemList()) {
                if(item.getFlag() == ConstantUtil.FLAG_NORMAL) {
                    itemIdList.add(item.getId());
                }
            }
        }
        return itemIdList;
    }

    public List<Optional> findAll() {
        return optionalRepository.findAllByFlagEquals(ConstantUtil.FLAG_NORMAL);
    }

    @Transactional
    public Optional addOptional(Optional optional, List<Integer> optionId) {
        if(optionId == null) optionId = new ArrayList<>();
        if(!validateOptionId(optionId)) return null;
        optional = optionalRepository.saveAndFlush(optional);
        optionalItemRepository.deleteOptionalItemByOptionalItemId_OptionalId(optional.getId());

        List<OptionalItem> optionalItemList = new ArrayList<>();
        for(int i=0; i<optionId.size(); i++) {
            OptionalItem.OptionalItemId optionalItemId = new OptionalItem.OptionalItemId();
            optionalItemId.setOptionalId(optional.getId());
            optionalItemId.setItemId(optionId.get(i));
            OptionalItem optionalItem = new OptionalItem();
            optionalItem.setOptionalItemId(optionalItemId);
            optionalItem.setNumber(i+1);
            optionalItemList.add(optionalItem);
        }
        optionalItemRepository.saveAllAndFlush(optionalItemList);
        return optional;
    }

    @Transactional
    public Optional updateOptional(Optional optional, List<Integer> optionId) {
        Optional oldOptional = optionalRepository.findById(optional.getId()).orElse(null);
        if(oldOptional == null || oldOptional.getFlag() != ConstantUtil.FLAG_NORMAL) return null;
        return addOptional(optional, optionId);
    }

    @Transactional
    public Optional deleteOptional(int id) {
        Optional optional = findById(id);
        if(optional == null) return null;
        optional.setFlag(ConstantUtil.FLAG_DELETE);
        optional = optionalRepository.saveAndFlush(optional);
        optionalItemRepository.deleteOptionalItemByOptionalItemId_OptionalId(optional.getId());
        return optional;
    }

    private boolean validateOptionId(List<Integer> optionId) {
        try {
            List<Item> itemList = itemRepository.findAllById(optionId);
            for(Item item : itemList) {
                if(!item.getCategory().equalsIgnoreCase(ConstantUtil.OPTION) || item.getFlag() != ConstantUtil.FLAG_NORMAL)
                    throw new IllegalArgumentException("Invalid Item ID: "+item.getId());
            }
            return true;
        }
        catch (IllegalArgumentException e) {
            log.error(e.getMessage());
        }
        return false;
    }

    public Map<Integer, Integer> optionalUsingCount() {
        Map<Integer, Integer> map = new HashMap<>();
        String query = "SELECT a.id, COUNT(b.optional_id) AS number FROM optional a LEFT JOIN item_optional b ON b.optional_id = a.id GROUP BY a.id";
        return getIntegerIntegerMap(map, query);
    }

    public Map<Integer, Integer> itemOptionUsingCount() {
        Map<Integer, Integer> map = new HashMap<>();
        String query = "SELECT a.id, COUNT(b.optional_id) AS number FROM item a LEFT JOIN optional_item b ON b.item_id = a.id WHERE category='OPTION' GROUP BY a.id";
        return getIntegerIntegerMap(map, query);
    }

    private Map<Integer, Integer> getIntegerIntegerMap(Map<Integer, Integer> map, String query) {
        @SuppressWarnings("unchecked")
        List<Object[]> resultList = entityManager.createNativeQuery(query).getResultList();
        for(Object[] o : resultList){
            int id = Integer.parseInt(o[0].toString());
            int number = Integer.parseInt(o[1].toString());
            map.put(id, number);
        }
        return map;
    }
}

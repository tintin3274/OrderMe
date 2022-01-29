package th.ku.orderme.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import th.ku.orderme.model.Item;
import th.ku.orderme.model.Optional;
import th.ku.orderme.repository.OptionalRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;


@Service
@AllArgsConstructor
public class OptionalService {
    @PersistenceContext
    private final EntityManager entityManager;
    private final OptionalRepository optionalRepository;

    public Optional findById(int id) {
        return optionalRepository.findById(id).orElse(null);
    }

    public List<Integer> getAllItemIdOfOptionalId(int optionalId) {
        List<Integer> itemIdList = new ArrayList<>();
        Optional optional = findById(optionalId);
        if(optional != null) {
            for(Item item : optional.getItemList()) {
                itemIdList.add(item.getId());
            }
        }
        return itemIdList;
    }

    public List<Optional> findAll() {
        return optionalRepository.findAll();
    }

    public boolean existsById(int id) {
        return optionalRepository.existsById(id);
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

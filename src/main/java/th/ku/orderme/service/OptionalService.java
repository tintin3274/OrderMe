package th.ku.orderme.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import th.ku.orderme.model.Item;
import th.ku.orderme.model.Optional;
import th.ku.orderme.repository.OptionalRepository;

import java.util.ArrayList;
import java.util.List;


@Service
@AllArgsConstructor
public class OptionalService {
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
}

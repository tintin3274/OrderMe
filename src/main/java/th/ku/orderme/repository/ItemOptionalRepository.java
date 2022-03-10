package th.ku.orderme.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import th.ku.orderme.model.ItemOptional;

import java.util.List;

public interface ItemOptionalRepository extends JpaRepository<ItemOptional, ItemOptional.ItemOptionalId> {
    List<ItemOptional> deleteItemOptionalByItemOptionalId_ItemId(int itemId);
}

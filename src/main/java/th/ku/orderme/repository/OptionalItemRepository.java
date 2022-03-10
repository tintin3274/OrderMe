package th.ku.orderme.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import th.ku.orderme.model.OptionalItem;

import java.util.List;

public interface OptionalItemRepository extends JpaRepository<OptionalItem, OptionalItem.OptionalItemId> {
    List<OptionalItem> deleteOptionalItemByOptionalItemId_OptionalId(int optionalId);
}

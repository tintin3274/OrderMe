package th.ku.orderme.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import th.ku.orderme.model.OptionalItem;

public interface OptionalItemRepository extends JpaRepository<OptionalItem, OptionalItem.OptionalItemId> {
}

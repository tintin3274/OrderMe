package th.ku.orderme.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import th.ku.orderme.model.SelectItem;

public interface SelectItemRepository extends JpaRepository<SelectItem, SelectItem.SelectItemId> {
}

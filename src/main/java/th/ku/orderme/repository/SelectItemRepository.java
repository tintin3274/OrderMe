package th.ku.orderme.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import th.ku.orderme.model.SelectItem;

import java.util.List;

public interface SelectItemRepository extends JpaRepository<SelectItem, SelectItem.SelectItemId> {
    List<SelectItem> findSelectItemsBySelectItemId_OrderId(int orderId);
}

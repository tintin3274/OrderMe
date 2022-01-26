package th.ku.orderme.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import th.ku.orderme.model.ItemOptional;

public interface ItemOptionalRepository extends JpaRepository<ItemOptional, ItemOptional.ItemOptionalId> {
}

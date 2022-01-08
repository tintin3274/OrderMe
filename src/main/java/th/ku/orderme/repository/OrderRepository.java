package th.ku.orderme.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import th.ku.orderme.model.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
}

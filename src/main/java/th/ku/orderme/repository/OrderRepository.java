package th.ku.orderme.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import th.ku.orderme.model.Order;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findAllByBill_Id(int billId);

    @Query(value = "SELECT DISTINCT bill_id FROM order_info WHERE status <> 'CANCEL' && status <> 'COMPLETE'", nativeQuery = true)
    List<Integer> getAllBillIdOfOrderNotCancelAndComplete();

    @Query("SELECT o.id FROM Order o WHERE o.status <> 'PENDING' AND o.status <> 'CANCEL' AND o.status <> 'COMPLETE'")
    List<Integer> getAllOrderIdNotPendingAndCancelAndComplete();
}

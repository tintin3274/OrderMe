package th.ku.orderme.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import th.ku.orderme.model.Bill;

@Repository
public interface BillRepository extends JpaRepository<Bill, Integer> {
}

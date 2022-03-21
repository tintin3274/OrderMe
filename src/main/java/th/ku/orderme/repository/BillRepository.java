package th.ku.orderme.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import th.ku.orderme.model.Bill;

import java.util.List;

@Repository
public interface BillRepository extends JpaRepository<Bill, Integer> {
    @Query("SELECT b.id FROM Bill b WHERE b.type like ?1")
    List<Integer> getAllIdByTypeEqual(String type);
}

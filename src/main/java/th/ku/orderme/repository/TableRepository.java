package th.ku.orderme.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import th.ku.orderme.model.Table;

import java.util.List;

@Repository
public interface TableRepository extends JpaRepository<Table, Integer> {
    Table findByBill_Id(int billId);

    @Query("SELECT t.id FROM Table t ORDER BY t.id")
    List<Integer> getAllId();

    @Query("SELECT t.bill.id FROM Table t")
    List<Integer> getAllBillId();
}

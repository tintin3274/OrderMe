package th.ku.orderme.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import th.ku.orderme.model.Table;

@Repository
public interface TableRepository extends JpaRepository<Table, Integer> {
}

package th.ku.orderme.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import th.ku.orderme.model.IndexCategory;

import java.util.List;

public interface IndexCategoryRepository extends JpaRepository<IndexCategory, String> {

    @Query("SELECT i.category FROM IndexCategory i ORDER BY i.number")
    List<String> getAllCategory();

}

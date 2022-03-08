package th.ku.orderme.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import th.ku.orderme.model.Item;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Integer> {
    List<Item> findItemByCategoryEqualsAndFlagEquals(String category, int flag);
    List<Item> findItemByCategoryNotAndFlagEquals(String category, int flag);

    @Query(value = "SELECT DISTINCT `category` FROM `item` WHERE `category` <> 'OPTION' AND `flag` = 0", nativeQuery = true)
    List<String> findAllFoodCategory();
}

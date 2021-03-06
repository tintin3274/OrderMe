package th.ku.orderme.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import th.ku.orderme.model.Optional;

import java.util.List;

@Repository
public interface OptionalRepository extends JpaRepository<Optional, Integer> {
    List<Optional> findAllByFlagEquals(int flag);
}

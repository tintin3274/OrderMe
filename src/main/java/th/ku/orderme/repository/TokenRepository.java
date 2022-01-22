package th.ku.orderme.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import th.ku.orderme.model.Token;

public interface TokenRepository extends JpaRepository<Token, String> {
}

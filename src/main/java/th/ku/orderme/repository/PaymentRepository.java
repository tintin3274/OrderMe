package th.ku.orderme.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import th.ku.orderme.model.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {
}

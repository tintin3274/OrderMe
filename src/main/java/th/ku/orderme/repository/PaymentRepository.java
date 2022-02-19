package th.ku.orderme.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import th.ku.orderme.model.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    Payment findByBill_Id(int billId);
    Payment findByRef1(String ref1);
}

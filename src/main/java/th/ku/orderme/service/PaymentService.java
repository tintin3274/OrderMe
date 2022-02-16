package th.ku.orderme.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import th.ku.orderme.repository.PaymentRepository;

@Service
@AllArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
}

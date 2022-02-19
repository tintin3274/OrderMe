package th.ku.orderme.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReceiptDTO {
    private BillDTO bill;
    private PaymentDTO payment;
}

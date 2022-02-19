package th.ku.orderme.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDTO {
    private String ref1;
    private String channel;
    private LocalDateTime createdTimestamp;
    private LocalDateTime updatedTimestamp;
    private double total;
}

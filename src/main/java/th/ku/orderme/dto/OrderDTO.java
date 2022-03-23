package th.ku.orderme.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {
    private int id;
    private String name;
    private String option;
    private int quantity;
    private double price;
    private double amount;
    private String comment;
    private String status;
    private LocalDateTime timestamp;
}

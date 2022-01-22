package th.ku.orderme.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BillDTO {
    private int id;
    private int person;
    private String type;
    private String status;
    private LocalDateTime timestamp;
    private List<OrderDTO> orders;
    private double total;
}

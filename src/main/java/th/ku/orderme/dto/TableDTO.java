package th.ku.orderme.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TableDTO {
    private int id;
    private boolean available;
    private int billId;
    private String token;
    private boolean paid;
}

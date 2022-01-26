package th.ku.orderme.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDTO {
    private int id;
    private String name;
    private String description;
    private String category;
    private double price;
    private int quantity;
    private boolean checkQuantity;
    private boolean display;
}

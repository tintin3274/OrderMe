package th.ku.orderme.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OptionalDTO {
    private int id;
    private String name;
    private String description;
    private int min;
    private int max;
}

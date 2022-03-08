package th.ku.orderme.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddOptionalDTO {
    private OptionalDTO optionGroup;
    private List<Integer> optionId;
}

package th.ku.orderme.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name="OPTIONAL_ITEM")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OptionalItem {
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Embeddable
    public static class OptionalItemId implements Serializable {
        @Column(name = "optional_id")
        private int optionalId;
        @Column(name = "item_id")
        private int itemId;
    }

    @EmbeddedId
    private OptionalItemId optionalItemId;
    private int number;
}

package th.ku.orderme.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name="ITEM_OPTIONAL")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemOptional {
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Embeddable
    public static class ItemOptionalId implements Serializable {
        @Column(name = "item_id")
        private int itemId;
        @Column(name = "optional_id")
        private int optionalId;
    }

    @EmbeddedId
    private ItemOptionalId itemOptionalId;
    private int number;
}

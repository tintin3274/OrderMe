package th.ku.orderme.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name="SELECT_ITEM")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SelectItem implements Serializable {
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Embeddable
    public static class SelectItemId implements Serializable {
        @Column(name = "order_id")
        private int orderId;
        @Column(name = "item_id")
        private int itemId;
    }

    @EmbeddedId
    private SelectItemId selectItemId;
    private int optionalId;
}

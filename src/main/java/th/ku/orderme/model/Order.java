package th.ku.orderme.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Table(name="ORDER_INFO")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order implements Serializable {

    @JsonView(Views.Overall.class)
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int id;

    @JsonView(Views.Overall.class)
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="bill_id")
    private Bill bill;

    @JsonView(Views.Overall.class)
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="item_id")
    private Item item;

    @JsonView(Views.Overall.class)
    private int quantity;

    @JsonView(Views.Overall.class)
    private String comment;

    @JsonView(Views.Overall.class)
    private String status;

    @JsonView(Views.Overall.class)
    private LocalDateTime timestamp;

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", item=" + item +
                ", quantity=" + quantity +
                ", comment='" + comment + '\'' +
                ", status='" + status + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}

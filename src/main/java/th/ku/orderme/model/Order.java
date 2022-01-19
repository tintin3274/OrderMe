package th.ku.orderme.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.repository.Query;

import javax.persistence.*;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Table(name="ORDER_INFO")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order implements Serializable {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int id;

    @JsonBackReference
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="bill_id")
    private Bill bill;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="item_id")
    private Item item;

    private int quantity;
    private String comment;
    private String status;
    private LocalDateTime timestamp;

//    @ManyToMany
//    @JoinTable(
//            name="SELECT_ITEM",
//            joinColumns=@JoinColumn(name="order_id", referencedColumnName="id"),
//            inverseJoinColumns=@JoinColumn(name="item_id", referencedColumnName="id"))
//    private List<Item> selectItemList;
}

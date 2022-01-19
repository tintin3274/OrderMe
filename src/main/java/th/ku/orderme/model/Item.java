package th.ku.orderme.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.List;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Table(name="ITEM")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Item implements Serializable {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int id;

    private String name;
    private String description;
    private String category;
    private String image;
    private double price;
    private int quantity;
    private boolean checkQuantity;
    private boolean display;

    @JsonIgnore
    @org.hibernate.annotations.OrderBy(clause="number, optional_id, item_id ASC")
    @ManyToMany
    @JoinTable(
            name="ITEM_OPTIONAL",
            joinColumns=@JoinColumn(name="item_id", referencedColumnName="id"),
            inverseJoinColumns=@JoinColumn(name="optional_id", referencedColumnName="id"))
    private List<Optional> optionalList;
}

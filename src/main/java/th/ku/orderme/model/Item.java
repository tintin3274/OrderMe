package th.ku.orderme.model;

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
    private String unit;
    private String category;
    private String image;
    private double price;
    private int quantity;
    private boolean checkQuantity;
    private boolean display;

    @org.hibernate.annotations.OrderBy(clause="NUMBER, OPTIONAL_ID, ITEM_ID ASC")
    @ManyToMany
    @JoinTable(
            name="ITEM_OPTIONAL",
            joinColumns=@JoinColumn(name="ITEM_ID", referencedColumnName="ID"),
            inverseJoinColumns=@JoinColumn(name="OPTIONAL_ID", referencedColumnName="ID"))
    private List<Optional> optionalList;
}

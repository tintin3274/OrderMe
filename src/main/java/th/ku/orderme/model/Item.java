package th.ku.orderme.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
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

    @JsonView(Views.Overall.class)
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int id;

    @JsonView(Views.Overall.class)
    private String name;

    @JsonView(Views.Overall.class)
    private String description;

    @JsonView(Views.Overall.class)
    private String category;

    @JsonView(Views.Overall.class)
    private String image;

    @JsonView(Views.Overall.class)
    private double price;

    @JsonView(Views.Overall.class)
    private int quantity;

    @JsonView(Views.Overall.class)
    private boolean checkQuantity;

    @JsonView(Views.Overall.class)
    private boolean display;

    @JsonIgnore
    @Version
    private long version;

    @JsonIgnore
    private int flag;

    @JsonView(Views.Detail.class)
    @org.hibernate.annotations.OrderBy(clause="number, optional_id, item_id ASC")
    @ManyToMany
    @JoinTable(
            name="ITEM_OPTIONAL",
            joinColumns=@JoinColumn(name="item_id", referencedColumnName="id"),
            inverseJoinColumns=@JoinColumn(name="optional_id", referencedColumnName="id"))
    private List<Optional> optionalList;

    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", category='" + category + '\'' +
                ", image='" + image + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                ", checkQuantity=" + checkQuantity +
                ", display=" + display +
                '}';
    }
}

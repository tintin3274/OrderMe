package th.ku.orderme.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name="OPTIONAL")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Optional implements Serializable {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int id;

    private String name;
    private String description;
    private int min;
    private int max;

    @org.hibernate.annotations.OrderBy(clause="number, optional_id, item_id ASC")
    @ManyToMany
    @JoinTable(
            name="OPTIONAL_ITEM",
            joinColumns=@JoinColumn(name="optional_id", referencedColumnName="id"),
            inverseJoinColumns=@JoinColumn(name="item_id", referencedColumnName="id"))
    private List<Item> itemList;

    @Override
    public String toString() {
        return "Optional{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", min=" + min +
                ", max=" + max +
                '}';
    }
}

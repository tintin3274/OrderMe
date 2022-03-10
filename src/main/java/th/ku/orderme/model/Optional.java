package th.ku.orderme.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
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

    @JsonView(Views.Overall.class)
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int id;

    @JsonView(Views.Overall.class)
    private String name;

    @JsonView(Views.Overall.class)
    private String description;

    @JsonView(Views.Overall.class)
    private int min;

    @JsonView(Views.Overall.class)
    private int max;

    @JsonIgnore
    private int flag;

    @JsonView(Views.Detail.class)
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

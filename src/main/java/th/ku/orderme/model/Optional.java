package th.ku.orderme.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
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

    @org.hibernate.annotations.OrderBy(clause="NUMBER, OPTIONAL_ID, ITEM_ID ASC")
    @ManyToMany
    @JoinTable(
            name="OPTIONAL_ITEM",
            joinColumns=@JoinColumn(name="OPTIONAL_ID", referencedColumnName="ID"),
            inverseJoinColumns=@JoinColumn(name="ITEM_ID", referencedColumnName="ID"))
    private List<Item> itemList;
}
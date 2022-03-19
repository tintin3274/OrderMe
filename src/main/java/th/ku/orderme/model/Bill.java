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
import java.time.LocalDateTime;
import java.util.List;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Table(name="BILL")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Bill implements Serializable {

    @JsonView(Views.Overall.class)
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int id;

    @JsonView(Views.Overall.class)
    private int person;

    @JsonView(Views.Overall.class)
    private String type;

    @JsonView(Views.Overall.class)
    private String status;

    @JsonView(Views.Overall.class)
    private LocalDateTime timestamp;

    @JsonView(Views.Detail.class)
    @JsonIgnore
    @OneToMany(mappedBy="bill")
    private List<Order> orderList;

    @Override
    public String toString() {
        return "Bill{" +
                "id=" + id +
                ", person=" + person +
                ", type='" + type + '\'' +
                ", status='" + status + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}

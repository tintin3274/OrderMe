package th.ku.orderme.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@javax.persistence.Table(name="TABLE_INFO")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Table implements Serializable {
    @Id
    private int id;

    private boolean available;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="BILL_ID")
    private Bill bill;
}

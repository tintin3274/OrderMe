package th.ku.orderme.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.persistence.Table;

@Entity
@Table(name="INDEX_CATEGORY")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class IndexCategory {
    @Id
    private String category;
    private int number;
}

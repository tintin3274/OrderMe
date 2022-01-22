package th.ku.orderme.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.persistence.Table;

@Entity
@Table(name="TOKEN")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Token {
    @Id
    private String id;

    @OneToOne(fetch= FetchType.LAZY)
    @JoinColumn(name="bill_id")
    private Bill bill;
}

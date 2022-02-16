package th.ku.orderme.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name="PAYMENT")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Payment implements Serializable {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int id;

    private String channel;
    private String ref1;
    private String ref2;
    private String ref3;
    private String status;
    private LocalDateTime createdTimestamp;
    private LocalDateTime updatedTimestamp;

    @OneToOne(fetch= FetchType.LAZY)
    @JoinColumn(name="bill_id")
    private Bill bill;
    private double total;
    private String receipt;
}

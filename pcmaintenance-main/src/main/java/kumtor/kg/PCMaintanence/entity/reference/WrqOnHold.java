package kumtor.kg.PCMaintanence.entity.reference;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "TW_WebWrqOnHoldReference", schema = "web")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class WrqOnHold {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;

    @Column(name = "WrqOnHoldCode", unique = true, nullable = false, columnDefinition = "NVARCHAR(30)")
    private String wrqOnHoldCode;

    @Column(name = "WrqOnHoldName", nullable = false, columnDefinition = "NVARCHAR(128)")
    private String wrqOnHoldName;

    @Column(name = "IsActive", nullable = false, columnDefinition = "CHAR(1)")
    private char isActive;

    @Column(name = "ChangedBy", nullable = false)
    private Integer changedBy;

    @Column(name = "ChangedDate", nullable = false)
    private LocalDateTime changedDate;
}

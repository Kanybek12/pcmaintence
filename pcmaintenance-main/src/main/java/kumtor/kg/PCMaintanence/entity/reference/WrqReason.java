package kumtor.kg.PCMaintanence.entity.reference;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "TW_WebWrqReasonReference", schema = "web")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class WrqReason {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;

    @Column(name = "WrqReasonCode", unique = true, nullable = false, columnDefinition = "NVARCHAR(30)")
    private String wrqReasonCode;

    @Column(name = "WrqReasonName", nullable = false, columnDefinition = "NVARCHAR(128)")
    private String wrqReasonName;

    @Column(name = "IsActive", nullable = false, columnDefinition = "CHAR(1)")
    private char isActive;

    @Column(name = "ChangedBy", nullable = false)
    private Integer changedBy;

    @Column(name = "ChangedDate", nullable = false)
    private LocalDateTime changedDate;
}

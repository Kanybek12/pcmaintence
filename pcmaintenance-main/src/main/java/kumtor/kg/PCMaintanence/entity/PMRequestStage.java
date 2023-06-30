package kumtor.kg.PCMaintanence.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import kumtor.kg.PCMaintanence.entity.reference.WrqLocation;
import kumtor.kg.PCMaintanence.entity.reference.WrqOnHold;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "TW_WebPMRequestStage", schema = "web")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PMRequestStage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PMRequestStageId")
    private Long stageId;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "PMRequest_Id", nullable = false, foreignKey = @ForeignKey(name = "FK_PMRequest_Id"))
    private PMRequest pmRequest;

    @ManyToOne
    @JoinColumn(name = "WrqLocation_Id", foreignKey = @ForeignKey(name = "FK_WrqLocation_Id"))
    private WrqLocation wrqLocation;

    @ManyToOne
    @JoinColumn(name = "WrqOnHold_Id", foreignKey = @ForeignKey(name = "FK_WrqOnHold_Id"))
    private WrqOnHold wrqOnHold;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    @Column(name = "StageDate")
    private LocalDateTime stageDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    @Column(name = "DateCreated", nullable = false)
    private LocalDateTime dateCreated;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    @Column(name = "DateChanged")
    private LocalDateTime dateChanged;

    @Column(name = "ForemanQty")
    private Integer foremanQty;

    @Column(name = "DetailComment", columnDefinition = "NVARCHAR(512)")
    private String detailComment;

    @Column(name = "CreatedBy", nullable = false)
    private Integer createdBy;

    @Column(name = "ChangedBy")
    private Integer changedBy;

    @Column(name = "StageCode", nullable = false)
    private Integer stageCode;

    @Column(name = "IsCanceled", columnDefinition = "CHAR(1)")
    private char isCanceled;

    @Column(name = "CancelReason", columnDefinition = "NVARCHAR(512)")
    private String cancelReason;
}

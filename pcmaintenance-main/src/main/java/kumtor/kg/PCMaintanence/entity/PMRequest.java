package kumtor.kg.PCMaintanence.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import kumtor.kg.PCMaintanence.entity.reference.WrqPriority;
import kumtor.kg.PCMaintanence.entity.reference.WrqReason;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;


@Entity
@Table(name = "TW_WebPMRequest", schema = "web")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class PMRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PMRequestId")
    private Long requestId;

    @Column(name = "AssetCode", nullable = false, columnDefinition = "CHAR(20)")
    private String assetCode;

    @Column(name = "AssetTypeCode", nullable = false, columnDefinition = "CHAR(20)")
    private String assetTypeCode;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    @Column(name = "StopDate", nullable = false)
    private LocalDateTime stopDate;

    @ManyToOne
    @JoinColumn(name = "WrqReason_Id", nullable = false, foreignKey = @ForeignKey(name = "FK_WrqReason_Id"))
    private WrqReason wrqReasonId;

    @Column(name = "WrqNo")
    private Integer wrqNo;

    @Column(name = "WrqTitle",columnDefinition = "NVARCHAR(250)")
    private String wrqTitle;

    @Column(name = "WrqReqComments", columnDefinition = "NVARCHAR(512)")
    private String wrqReqComments;

    @ManyToOne
    @JoinColumn(name = "WrqPriority_Id", nullable = false, foreignKey = @ForeignKey(name = "FK_WrqPriority_Id"))
    private WrqPriority priorityId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    @Column(name = "DateExpected")
    private LocalDateTime dateExpected;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    @Column(name = "DateFinished")
    private LocalDateTime dateFinished;

    @Column(name = "StatusCode")
    private Integer statusCode;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @CreationTimestamp
    @Column(name = "DateCreated", nullable = false)
    private LocalDateTime createdDate;

    @Column(name = "CreatedBy", nullable = false)
    private Integer createdBy;

    @Column(name = "ChangedBy")
    private Integer changedBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @UpdateTimestamp
    @Column(name = "DateChanged")
    private LocalDateTime changedDate;
}

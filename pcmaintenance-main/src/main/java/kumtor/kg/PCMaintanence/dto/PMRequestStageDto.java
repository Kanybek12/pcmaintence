package kumtor.kg.PCMaintanence.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PMRequestStageDto {

    private Long stageId;

    private Integer stageCode;

    private String stageMessage;

    private String locationCode;

    private String locationName;

    private String onHoldCode;

    private String onHoldName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime stageDate;

    private Integer foremanQty;

    private String comment;

    private String history;

    private char isCanceled;

    private String cancelReason;
}

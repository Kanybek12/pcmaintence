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
public class PMRequestDto {

    private Long requestId;

    private String assetCode;

    private String assetTypeCode;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime stopDate;

    private String wrqReasonCode;

    private String wrqReasonName;

    private Integer wrqNo;

    private String wrqTitle;

    private String reqComments;

    private String wrqPriorityCode;

    private String wrqPriorityName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime dateExpected;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime dateFinished;

    private Integer statusCode;

    private String statusMessage;
}

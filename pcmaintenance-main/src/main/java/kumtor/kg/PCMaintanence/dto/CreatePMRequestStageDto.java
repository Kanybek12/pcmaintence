package kumtor.kg.PCMaintanence.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreatePMRequestStageDto {

    private String locationCode;

    private String onHoldCode;

    private Integer foremanQty;

    private String comment;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    @NotNull
    private LocalDateTime stageDate;

    @NotNull
    private Integer stageCode;

}

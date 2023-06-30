package kumtor.kg.PCMaintanence.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreatePMRequestDto {

    @NotBlank
    private String assetCode;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime stopDate;

    @NotBlank
    private String wrqReasonCode;

    private String wrqNo;

    private String wrqTitle;

    private String reqComments;

    @NotBlank
    private String wrqPriorityCode;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime dateExpected;
}

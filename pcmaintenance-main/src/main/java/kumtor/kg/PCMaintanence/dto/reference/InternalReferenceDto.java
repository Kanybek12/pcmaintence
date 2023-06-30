package kumtor.kg.PCMaintanence.dto.reference;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InternalReferenceDto {

    private Integer id;

    @Size(max = 30)
    @NotBlank
    private String code;

    @NotBlank
    private String name;

    private char isActive;

}

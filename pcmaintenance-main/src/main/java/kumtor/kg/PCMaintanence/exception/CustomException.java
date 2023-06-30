package kumtor.kg.PCMaintanence.exception;


import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class CustomException extends RuntimeException{

    private int code;

    private String message;

}

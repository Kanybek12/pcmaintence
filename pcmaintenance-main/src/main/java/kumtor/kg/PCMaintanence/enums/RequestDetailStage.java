package kumtor.kg.PCMaintanence.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RequestDetailStage {

    STOP(110, "Остановка"),
    REPAIR(111, "Ремонт"),
    WAITING(112, "Ожидание"),
    COMPLETION(113, "Завершение");

    private Integer code;
    private String message;

    public static RequestDetailStage getStage(int code){
        for (RequestDetailStage s : RequestDetailStage.values()) {
            if(code == s.code){
                return s;
            }
        }
        return STOP;
    }
}

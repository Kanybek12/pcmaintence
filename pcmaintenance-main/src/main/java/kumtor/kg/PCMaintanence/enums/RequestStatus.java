package kumtor.kg.PCMaintanence.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum RequestStatus {

    IN_WORK(100, "В работе"),
    WAITING(101, "Ожидание"),
    COMPLETED(102, "Завершен"),
    CANCEL(103, "Заявка отменена");

    private Integer code;
    private String message;

    public static RequestStatus getStatus(int code){
        for (RequestStatus s : RequestStatus.values()) {
            if(code == s.code){
                return s;
            }
        }
        return IN_WORK;
    }
}

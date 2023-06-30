package kumtor.kg.PCMaintanence.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponseEnum {

    OK(200, "ОК"),
    REQUEST_NOT_FOUND(201, "Заявка не найдена"),
    REQUEST_DETAIL_NOT_FOUND(202, "Этап не найден"),
    REQUEST_DELETED(203, "Заявка успешно удалена"),
    REQUEST_CANCELED(204, "Заявка успешно отменена"),
    STAGE_DELETED(205, "Этап успешно удален"),
    STAGE_CANCELED(206, "Этап успешно отменен"),
    REQUEST_COMPLETED(207, "Заявка уже завершена"),
    REQUEST_ALREADY_CANCELED(208, "Заявка уже отменена"),
    STAGE_ALREADY_CANCELED(209, "Этап уже отменен");

    private int code;
    private String message;

}

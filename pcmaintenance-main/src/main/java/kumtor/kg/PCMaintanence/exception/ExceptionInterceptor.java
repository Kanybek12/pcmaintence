package kumtor.kg.PCMaintanence.exception;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
@Slf4j
public class ExceptionInterceptor extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException exception,
                                                                     ServletWebRequest webRequest) throws IOException {
        return ResponseEntity.ok(new ApiException(HttpStatus.BAD_REQUEST.value(), exception.getLocalizedMessage()));
    }


    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {
        List<String> errors = new ArrayList<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.add("Поле " + error.getField() + " не может быть пустым");
        }
        log.error("Error : {}  ", errors);
        return ResponseEntity.ok(new ApiException(HttpStatus.BAD_REQUEST.value(), errors.toString().replaceAll("(^\\[|]$)", "")));
    }

    @ExceptionHandler(value = {CustomException.class})
    public final ResponseEntity<Object> handleCustomException(CustomException e) {
        log.error("Error : {}", e.getMessage());
        return ResponseEntity.ok(new ApiException(e.getCode(), e.getMessage()));
    }

    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<Object> handleException(Exception e) {

        if (e.getLocalizedMessage().equals("Access is denied")) {
            log.error("Error : {}", e.getMessage());
            return ResponseEntity.ok(new ApiException(HttpStatus.FORBIDDEN.value(), "Доступ запрещен"));
        }
        log.error("Error : {}", e.getMessage());
        return ResponseEntity.ok(new ApiException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Внутренняя ошибка сервера"));
    }


    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        String error = "Отсутствует запрашиваемый параметр : " + ex.getParameterName();
        log.error("Error : {}", error);
        return new ResponseEntity<>(new ApiException(HttpStatus.BAD_REQUEST.value(), error), HttpStatus.OK);
    }
}
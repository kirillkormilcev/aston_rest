package ru.aston.rest_service.exception.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

/**
 * Класс для формирования
 * объекта ошибки
 * для отправки пользователю
 */
@Slf4j
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    List<String> errors;
    int status;
    String message;
    String reason;
    String timestamp;

    /**
     * Фильтрует ошибки из стек-трейса
     * @param e тело ошибки
     * @return список строк
     */
    public static List<String> logAndGetErrorsFromStackTrace(Exception e) {
        return Arrays.stream(ExceptionUtils.getRootCauseStackTrace(e)).filter(f -> f.contains("ru.aston.rest_service"))
                .map(string -> {
                    if (string.contains("\t")) {
                        string = string.substring(1);
                    }
                    return string;
                })
                .toList();
    }

    public static ErrorResponse createError(HttpServletResponse resp, Exception e) {
        log.warn(e.getMessage(), e.getCause());
        return new ErrorResponse(logAndGetErrorsFromStackTrace(e),
                resp.getStatus(), e.getMessage(), e.getClass().toString());
    }

    public ErrorResponse(List<String> errors, int status, String message, String reason) {
        this.errors = errors;
        this.status = status;
        this.message = message;
        this.reason = reason;
        this.timestamp = dateTimeFormat(LocalDateTime.now());
    }

    private String dateTimeFormat(LocalDateTime now) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return now.format(formatter);
    }
}
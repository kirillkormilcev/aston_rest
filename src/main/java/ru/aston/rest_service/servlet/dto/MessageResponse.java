package ru.aston.rest_service.servlet.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

/**
 * Класс для формирования
 * объекта сообщения
 * для отправки пользователю
 */
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MessageResponse {
    String message;

    public static MessageResponse createMessage(String message) {
        return new MessageResponse(message);
    }

    public MessageResponse(String message) {
        this.message = message;
    }
}
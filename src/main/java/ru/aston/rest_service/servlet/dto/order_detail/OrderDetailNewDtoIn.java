package ru.aston.rest_service.servlet.dto.order_detail;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.aston.rest_service.model.OrderStatus;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class OrderDetailNewDtoIn {
    @NotNull
    @NotBlank
    OrderStatus status;
    @NotNull
    List<Long> productIds;
}
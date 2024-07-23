package ru.aston.rest_service.servlet.dto.order_detail;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.aston.rest_service.model.OrderStatus;
import ru.aston.rest_service.servlet.dto.product.ProductShortDtoOut;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@ToString
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class OrderDetailDtoOut {
    OrderStatus status;
    List<ProductShortDtoOut> products;
    BigDecimal totalAmount;
}
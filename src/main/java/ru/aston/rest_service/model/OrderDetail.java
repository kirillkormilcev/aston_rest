package ru.aston.rest_service.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;

/**
 * Order Details
 * Relations:
 * OtM: -> Product
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@ToString
public class OrderDetail {
    Long id;
    OrderStatus status;
    List<Long> productIds;
    BigDecimal totalAmount;

    /**
     * for test
     * */
    public OrderDetail(Long id, OrderStatus status) {
        this.id = id;
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OrderDetail that = (OrderDetail) o;
        return id.equals(that.id) && status == that.status && totalAmount.equals(that.totalAmount);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + status.hashCode();
        result = 31 * result + totalAmount.hashCode();
        return result;
    }
}
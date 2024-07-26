package ru.aston.rest_service.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * Order approve
 * Relations:
 * OtO -> OrderDetail
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@ToString
public class OrderApproval {
    Long id;
    OrderDetail orderDetail;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OrderApproval that = (OrderApproval) o;
        return id.equals(that.id) && orderDetail.equals(that.orderDetail);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + orderDetail.hashCode();
        return result;
    }
}
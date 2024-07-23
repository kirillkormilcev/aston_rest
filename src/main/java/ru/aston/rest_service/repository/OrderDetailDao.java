package ru.aston.rest_service.repository;

import ru.aston.rest_service.model.OrderDetail;

import java.util.List;

public interface OrderDetailDao extends Dao<OrderDetail, Long> {

    /**
     * Метод проверки существования
     * продукта в заказах
     * @param productId id продукта
     * @return булево значение
     */
    boolean existProductInOrderDetailsByID(Long productId);

    List<OrderDetail> findAll();

    List<OrderDetail> findAllWithCondition(String condition);

    boolean exitsById(Long id);
}
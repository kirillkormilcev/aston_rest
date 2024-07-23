package ru.aston.rest_service.service;

import ru.aston.rest_service.exception.NotFoundException;
import ru.aston.rest_service.servlet.dto.order_detail.OrderDetailDtoOut;
import ru.aston.rest_service.servlet.dto.order_detail.OrderDetailNewDtoIn;
import ru.aston.rest_service.servlet.dto.order_detail.OrderDetailUpdateDtoIn;

import java.util.List;

public interface OrderDetailService {

    OrderDetailDtoOut save(OrderDetailNewDtoIn orderDetailNewDtoIn);

    void update(OrderDetailUpdateDtoIn orderDetailUpdateDtoIn) throws NotFoundException;

    OrderDetailDtoOut findById(Long orderDetailId) throws NotFoundException;

    List<OrderDetailDtoOut> findAll();

    List<OrderDetailDtoOut> findAllWithCondition(String condition);

    Boolean delete(Long orderDetailId) throws NotFoundException;
}
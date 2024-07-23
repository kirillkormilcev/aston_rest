package ru.aston.rest_service.service.impl;

import ru.aston.rest_service.exception.NotFoundException;
import ru.aston.rest_service.exception.ValidationException;
import ru.aston.rest_service.model.OrderDetail;
import ru.aston.rest_service.repository.OrderDetailDao;
import ru.aston.rest_service.repository.ProductDao;
import ru.aston.rest_service.repository.impl.OrderDetailDaoImpl;
import ru.aston.rest_service.repository.impl.ProductDaoImpl;
import ru.aston.rest_service.service.OrderDetailService;
import ru.aston.rest_service.servlet.dto.order_detail.OrderDetailDtoOut;
import ru.aston.rest_service.servlet.dto.order_detail.OrderDetailNewDtoIn;
import ru.aston.rest_service.servlet.dto.order_detail.OrderDetailUpdateDtoIn;
import ru.aston.rest_service.servlet.mapper.OrderDetailMapper;

import java.util.HashSet;
import java.util.List;

public class OrderDetailServiceImpl implements OrderDetailService {
    private final OrderDetailDao orderDetailRepository = OrderDetailDaoImpl.getOrderDetailRepository();
    private final ProductDao productRepository = ProductDaoImpl.getProductRepository();
    private static final OrderDetailMapper orderDetailMapper = OrderDetailMapper.INSTANCE;
    private static OrderDetailService orderDetailService;

    public static synchronized OrderDetailService getInstance() {
        if (orderDetailService == null) {
            orderDetailService = new OrderDetailServiceImpl();
        }
        return orderDetailService;
    }

    @Override
    public OrderDetailDtoOut save(OrderDetailNewDtoIn orderDetailNewDtoIn) {
        validationOrderDetail(orderDetailNewDtoIn);
        OrderDetail orderDetail = orderDetailRepository.save(orderDetailMapper.orderDetailNewDtoInToOrderDetail(orderDetailNewDtoIn));
        return orderDetailMapper.orderDetailToOrderDetailDtoOut(orderDetail);
    }

    @Override
    public void update(OrderDetailUpdateDtoIn orderDetailUpdateDtoIn) throws NotFoundException {
        if (orderDetailUpdateDtoIn == null || orderDetailUpdateDtoIn.getId() == null) {
            throw new IllegalArgumentException();
        }
        checkOrderDetailId(orderDetailUpdateDtoIn.getId());
        orderDetailRepository.update(orderDetailMapper.orderDetailUpdateDtoInToOrderDetail(orderDetailUpdateDtoIn));
    }

    @Override
    public OrderDetailDtoOut findById(Long orderDetailId) throws NotFoundException {
        checkOrderDetailId(orderDetailId);
        return orderDetailMapper.orderDetailToOrderDetailDtoOut(orderDetailRepository.findById(orderDetailId).orElseThrow());
    }

    @Override
    public List<OrderDetailDtoOut> findAll() {
        List<OrderDetail> orderDetails = orderDetailRepository.findAll();
        return orderDetailMapper.orderDetailDtoOutListToOrderDetailDtoOutList(orderDetails);
    }

    @Override
    public List<OrderDetailDtoOut> findAllWithCondition(String condition) {
        List<OrderDetail> orderDetails = orderDetailRepository.findAllWithCondition(condition);
        return orderDetailMapper.orderDetailDtoOutListToOrderDetailDtoOutList(orderDetails);
    }

    @Override
    public Boolean delete(Long orderDetailId) throws NotFoundException {
        checkOrderDetailId(orderDetailId);
        return orderDetailRepository.deleteById(orderDetailId);
    }

    private void validationOrderDetail(OrderDetailNewDtoIn orderDetailNewDtoIn) {
        if (!new HashSet<>(productRepository.findAllIds()).containsAll(orderDetailNewDtoIn.getProductIds())) {
            throw new ValidationException("Продуктов, содержащихся в заказе, еще нет в базе данных, либо не доступен.");
        }
    }

    private void checkOrderDetailId(Long orderDetailId) throws NotFoundException {
        if (!orderDetailRepository.exitsById(orderDetailId)) {
            throw new NotFoundException("Заказ с таким id = " + orderDetailId + " не найден.");
        }
    }
}
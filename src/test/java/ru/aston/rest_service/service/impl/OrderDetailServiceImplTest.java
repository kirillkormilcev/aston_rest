package ru.aston.rest_service.service.impl;

import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import ru.aston.rest_service.exception.NotFoundException;
import ru.aston.rest_service.model.OrderDetail;
import ru.aston.rest_service.model.OrderStatus;
import ru.aston.rest_service.repository.OrderDetailDao;
import ru.aston.rest_service.repository.ProductDao;
import ru.aston.rest_service.repository.impl.OrderDetailDaoImpl;
import ru.aston.rest_service.repository.impl.ProductDaoImpl;
import ru.aston.rest_service.service.OrderDetailService;
import ru.aston.rest_service.servlet.dto.order_detail.OrderDetailDtoOut;
import ru.aston.rest_service.servlet.dto.order_detail.OrderDetailNewDtoIn;
import ru.aston.rest_service.servlet.dto.order_detail.OrderDetailUpdateDtoIn;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class OrderDetailServiceImplTest {
    public static OrderDetailService orderDetailService;
    private static OrderDetailDao mockOrderDetailRepository;
    private static ProductDao mockProductRepository;
    private static ProductDaoImpl productDaoInstance;
    private static OrderDetailDaoImpl orderDetailDaoInstance;

    private static void setMock(OrderDetailDao mock) {
        try {
            Field instance = OrderDetailDaoImpl.class.getDeclaredField("orderDetailRepository");
            instance.setAccessible(true);
            orderDetailDaoInstance = (OrderDetailDaoImpl) instance.get(instance);
            instance.set(instance, mock);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void setMock(ProductDao mock) {
        try {
            Field instance = ProductDaoImpl.class.getDeclaredField("productRepository");
            instance.setAccessible(true);
            productDaoInstance = (ProductDaoImpl) instance.get(instance);
            instance.set(instance, mock);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeAll
    static void beforeAll() {
        mockOrderDetailRepository = Mockito.mock(OrderDetailDao.class);
        setMock(mockOrderDetailRepository);
        mockProductRepository = Mockito.mock(ProductDao.class);
        setMock(mockProductRepository);

        orderDetailService = OrderDetailServiceImpl.getInstance();
    }

    @AfterAll
    static void afterAll() throws Exception {
        Field instance = OrderDetailDaoImpl.class.getDeclaredField("orderDetailRepository");
        instance.setAccessible(true);
        instance.set(instance, orderDetailDaoInstance);

        instance = ProductDaoImpl.class.getDeclaredField("productRepository");
        instance.setAccessible(true);
        instance.set(instance, productDaoInstance);
    }

    @BeforeEach
    void setUp() {
        Mockito.reset(mockOrderDetailRepository);
    }

    @Test
    void save() {
        Long id = 1L;
        OrderStatus expectedStatus = OrderStatus.CREATED;

        OrderDetailNewDtoIn dto = new OrderDetailNewDtoIn(OrderStatus.CREATED, List.of());
        OrderDetail orderDetail = new OrderDetail(id, OrderStatus.CREATED);

        Mockito.doReturn(orderDetail).when(mockOrderDetailRepository).save(Mockito.any(OrderDetail.class));

        OrderDetailDtoOut result = orderDetailService.save(dto);

        Assertions.assertEquals(expectedStatus, result.getStatus());
    }

    @Test
    void update() throws NotFoundException {
        Long expectedId = 2L;

        OrderDetailUpdateDtoIn dto = new OrderDetailUpdateDtoIn(expectedId, OrderStatus.PAID, List.of());

        Mockito.doReturn(true).when(mockOrderDetailRepository).exitsById(Mockito.anyLong());


        orderDetailService.update(dto);

        ArgumentCaptor<OrderDetail> argument = ArgumentCaptor.forClass(OrderDetail.class);
        Mockito.verify(mockOrderDetailRepository).update(argument.capture());

        OrderDetail result = argument.getValue();
        Assertions.assertEquals(expectedId, result.getId());
     }

    @Test
    void updateNotFound() {
        Long expectedId = 1L;

        OrderDetailUpdateDtoIn dto = new OrderDetailUpdateDtoIn(expectedId, OrderStatus.PAID, List.of());

        Mockito.doReturn(false).when(mockOrderDetailRepository).exitsById(Mockito.anyLong());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> orderDetailService.update(dto), "Non found."
        );
        Assertions.assertEquals("Заказ с таким id = " + expectedId + " не найден.", exception.getMessage());
    }

    @Test
    void findById() throws NotFoundException {
        Long id = 1L;
        OrderStatus expectedStatus = OrderStatus.CREATED;

        Optional<OrderDetail> orderDetail = Optional.of(new OrderDetail(id, expectedStatus));

        Mockito.doReturn(true).when(mockOrderDetailRepository).exitsById(Mockito.anyLong());
        Mockito.doReturn(orderDetail).when(mockOrderDetailRepository).findById(Mockito.anyLong());

        OrderDetailDtoOut dto = orderDetailService.findById(id);

        Assertions.assertEquals(expectedStatus, dto.getStatus());
    }

    @Test
    void findByIdNotFound(){
        Long expectedId = 1L;

        Mockito.doReturn(false).when(mockOrderDetailRepository).exitsById(Mockito.anyLong());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> orderDetailService.findById(expectedId), "Not found."
        );
        Assertions.assertEquals("Заказ с таким id = " + expectedId + " не найден.", exception.getMessage());
    }

    @Test
    void findAll() {
        orderDetailService.findAll();
        Mockito.verify(mockOrderDetailRepository).findAll();
    }

    @Test
    void findAllWithCondition() {
        orderDetailService.findAllWithCondition("condition");
        Mockito.verify(mockOrderDetailRepository).findAllWithCondition("condition");
    }

    @Test
    void delete() throws NotFoundException {
        Long expectedId = 1L;

        Mockito.doReturn(true).when(mockOrderDetailRepository).exitsById(Mockito.anyLong());
        orderDetailService.delete(expectedId);

        ArgumentCaptor<Long> argument = ArgumentCaptor.forClass(Long.class);
        Mockito.verify(mockOrderDetailRepository).deleteById(argument.capture());

        Long result = argument.getValue();
        Assertions.assertEquals(expectedId, result);
    }
}
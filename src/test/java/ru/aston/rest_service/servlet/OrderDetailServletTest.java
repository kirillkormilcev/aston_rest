package ru.aston.rest_service.servlet;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.aston.rest_service.exception.NotFoundException;
import ru.aston.rest_service.model.OrderStatus;
import ru.aston.rest_service.service.OrderDetailService;
import ru.aston.rest_service.service.impl.OrderDetailServiceImpl;
import ru.aston.rest_service.servlet.dto.order_detail.OrderDetailNewDtoIn;
import ru.aston.rest_service.servlet.dto.order_detail.OrderDetailUpdateDtoIn;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.reflect.Field;

@ExtendWith(MockitoExtension.class)
class OrderDetailServletTest {
    private static OrderDetailService mockOrderDetailService;
    @InjectMocks
    private static OrderDetailServlet orderDetailServlet;
    private static OrderDetailServiceImpl orderDetailServiceInstance;
    @Mock
    private HttpServletRequest mockRequest;
    @Mock
    private HttpServletResponse mockResponse;
    @Mock
    private BufferedReader mockBufferedReader;

    private static void setMock(OrderDetailService mock) {
        try {
            Field instance = OrderDetailServiceImpl.class.getDeclaredField("orderDetailService");
            instance.setAccessible(true);
            orderDetailServiceInstance = (OrderDetailServiceImpl) instance.get(instance);
            instance.set(instance, mock);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeAll
    static void beforeAll() {
        mockOrderDetailService = Mockito.mock(OrderDetailService.class);
        setMock(mockOrderDetailService);
        orderDetailServlet = new OrderDetailServlet();
    }

    @AfterAll
    static void afterAll() throws NoSuchFieldException, IllegalAccessException {
        Field instance = OrderDetailServiceImpl.class.getDeclaredField("orderDetailService");
        instance.setAccessible(true);
        instance.set(instance, orderDetailServiceInstance);
    }

    @BeforeEach
    void setUp() throws IOException {
        Mockito.doReturn(new PrintWriter(Writer.nullWriter())).when(mockResponse).getWriter();
    }

    @BeforeEach
    void tearDown() {
        Mockito.reset(mockOrderDetailService);
    }

    @Test
    void doGetAll() throws ServletException, IOException {
        Mockito.doReturn("order/all").when(mockRequest).getPathInfo();

        orderDetailServlet.doGet(mockRequest, mockResponse);

        Mockito.verify(mockOrderDetailService).findAll();
    }

    @Test
    void doGetByID() throws ServletException, IOException, NotFoundException {
        Mockito.doReturn("order/1").when(mockRequest).getPathInfo();

        orderDetailServlet.doGet(mockRequest, mockResponse);

        Mockito.verify(mockOrderDetailService).findById(Mockito.anyLong());
    }

    @Test
    void doGetNotFound() throws NotFoundException, ServletException, IOException {
        Mockito.doReturn("order/100").when(mockRequest).getPathInfo();
        Mockito.doThrow(new NotFoundException("not found")).when(mockOrderDetailService).findById(100L);

        orderDetailServlet.doGet(mockRequest, mockResponse);

        Mockito.verify(mockResponse).setStatus(HttpServletResponse.SC_NOT_FOUND);
    }

    @Test
    void doGetBadRequest() throws ServletException, IOException {
        Mockito.doReturn("order/d100").when(mockRequest).getPathInfo();

        orderDetailServlet.doGet(mockRequest, mockResponse);

        Mockito.verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    void doPost() throws IOException, ServletException {
        OrderStatus expectedStatus = OrderStatus.CREATED;

        Mockito.doReturn(mockBufferedReader).when(mockRequest).getReader();
        Mockito.doReturn(
                "{\"status\":\"" + expectedStatus + "\"}",
                null
        ).when(mockBufferedReader).readLine();

        orderDetailServlet.doPost(mockRequest, mockResponse);

        ArgumentCaptor<OrderDetailNewDtoIn> argumentCaptor = ArgumentCaptor.forClass(OrderDetailNewDtoIn.class);
        Mockito.verify(mockOrderDetailService).save(argumentCaptor.capture());

        OrderDetailNewDtoIn result = argumentCaptor.getValue();
        Assertions.assertEquals(expectedStatus, result.getStatus());
    }

    @Test
    void doPut() throws IOException, NotFoundException, ServletException {
        OrderStatus expectedStatus = OrderStatus.CREATED;

        Mockito.doReturn(mockBufferedReader).when(mockRequest).getReader();
        Mockito.doReturn(
                "{\"status\":\"" + expectedStatus + "\"}",
                null
        ).when(mockBufferedReader).readLine();

        Mockito.doNothing().when(mockOrderDetailService).update(Mockito.any(OrderDetailUpdateDtoIn.class));

        orderDetailServlet.doPut(mockRequest, mockResponse);

        Mockito.verify(mockOrderDetailService).update(Mockito.any(OrderDetailUpdateDtoIn.class));

    }

    @Test
    void doPutBadRequest() throws IOException, ServletException {
        Mockito.doReturn(mockBufferedReader).when(mockRequest).getReader();
        Mockito.doReturn(
                "{Bad json:1}",
                null
        ).when(mockBufferedReader).readLine();

        orderDetailServlet.doPut(mockRequest, mockResponse);

        Mockito.verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    void doDelete() throws ServletException, IOException, NotFoundException {
        Mockito.doReturn("order/2").when(mockRequest).getPathInfo();
        Mockito.doReturn(true).when(mockOrderDetailService).delete(Mockito.anyLong());

        orderDetailServlet.doDelete(mockRequest, mockResponse);

        Mockito.verify(mockOrderDetailService).delete(Mockito.anyLong());
        Mockito.verify(mockResponse).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    void doDeleteBadRequest() throws ServletException, IOException {
        Mockito.doReturn("order/d100").when(mockRequest).getPathInfo();

        orderDetailServlet.doDelete(mockRequest, mockResponse);

        Mockito.verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }
}
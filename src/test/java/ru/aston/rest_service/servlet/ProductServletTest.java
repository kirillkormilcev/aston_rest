package ru.aston.rest_service.servlet;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.aston.rest_service.exception.NotFoundException;
import ru.aston.rest_service.service.ProductService;
import ru.aston.rest_service.service.impl.ProductServiceImpl;
import ru.aston.rest_service.servlet.dto.product.ProductNewDtoIn;
import ru.aston.rest_service.servlet.dto.product.ProductUpdateDtoIn;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.reflect.Field;

@ExtendWith(MockitoExtension.class)
class ProductServletTest {
    private static ProductService mockProductService;
    @InjectMocks
    private static ProductServlet productServlet;
    private static ProductServiceImpl productServiceInstance;
    @Mock
    private HttpServletRequest mockRequest;
    @Mock
    private HttpServletResponse mockResponse;
    @Mock
    private BufferedReader mockBufferedReader;

    private static void setMock(ProductService mock) {
        try {
            Field instance = ProductServiceImpl.class.getDeclaredField("productService");
            instance.setAccessible(true);
            productServiceInstance = (ProductServiceImpl) instance.get(instance);
            instance.set(instance, mock);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeAll
    static void beforeAll() {
        mockProductService = Mockito.mock(ProductService.class);
        setMock(mockProductService);
        productServlet = new ProductServlet();
    }

    @AfterAll
    static void afterAll() throws IllegalAccessException, NoSuchFieldException {
        Field instance = ProductServiceImpl.class.getDeclaredField("productService");
        instance.setAccessible(true);
        instance.set(instance, productServiceInstance);
    }

    @BeforeEach
    void setUp() throws IOException {
        Mockito.doReturn(new PrintWriter(Writer.nullWriter())).when(mockResponse).getWriter();
    }

    @BeforeEach
    void tearDown() {
        Mockito.reset(mockProductService);
    }

    @Test
    void doGetAll() throws ServletException, IOException {
        Mockito.doReturn("product/all").when(mockRequest).getPathInfo();

        productServlet.doGet(mockRequest, mockResponse);

        Mockito.verify(mockProductService).findAll();
    }

    @Test
    void doGetById() throws ServletException, IOException, NotFoundException {
        Mockito.doReturn("product/2").when(mockRequest).getPathInfo();

        productServlet.doGet(mockRequest, mockResponse);

        Mockito.verify(mockProductService).findById(Mockito.anyLong());
    }

    @Test
    void doGetNotFound() throws ServletException, IOException, NotFoundException {
        Mockito.doReturn("product/200").when(mockRequest).getPathInfo();
        Mockito.doThrow(new NotFoundException("not found")).when(mockProductService).findById(200L);

        productServlet.doGet(mockRequest, mockResponse);

        Mockito.verify(mockResponse).setStatus(HttpServletResponse.SC_NOT_FOUND);
    }

    @Test
    void doGetBadRequest() throws ServletException, IOException {
        Mockito.doReturn("product/2r").when(mockRequest).getPathInfo();

        productServlet.doGet(mockRequest, mockResponse);

        Mockito.verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    void doPost() throws IOException, ServletException {
        String expectedProductName = "new product";
        Mockito.doReturn(mockBufferedReader).when(mockRequest).getReader();
        Mockito.doReturn(
                "{\"name\":\"" + expectedProductName + "\"}",
                null
        ).when(mockBufferedReader).readLine();

        productServlet.doPost(mockRequest, mockResponse);

        ArgumentCaptor<ProductNewDtoIn> argumentCaptor = ArgumentCaptor.forClass(ProductNewDtoIn.class);
        Mockito.verify(mockProductService).save(argumentCaptor.capture());

        ProductNewDtoIn result = argumentCaptor.getValue();
        Assertions.assertEquals(expectedProductName, result.getName());
    }

    @Test
    void doPut() throws IOException, ServletException, NotFoundException {
        String expectedProductName = "update product";

        Mockito.doReturn(mockBufferedReader).when(mockRequest).getReader();
        Mockito.doReturn(
                "{\"id\": 4,\"name\": \"" +
                        expectedProductName + "\"}",
                null
        ).when(mockBufferedReader).readLine();

        productServlet.doPut(mockRequest, mockResponse);

        ArgumentCaptor<ProductUpdateDtoIn> argumentCaptor = ArgumentCaptor.forClass(ProductUpdateDtoIn.class);
        Mockito.verify(mockProductService).update(argumentCaptor.capture());

        ProductUpdateDtoIn result = argumentCaptor.getValue();
        Assertions.assertEquals(expectedProductName, result.getName());
    }

    @Test
    void doPutBadRequest() throws IOException, ServletException {
        Mockito.doReturn(mockBufferedReader).when(mockRequest).getReader();
        Mockito.doReturn(
                "{Bad json:1}",
                null
        ).when(mockBufferedReader).readLine();

        productServlet.doPut(mockRequest, mockResponse);

        Mockito.verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    void doDelete() throws ServletException, IOException, NotFoundException {
        Mockito.doReturn("product/2").when(mockRequest).getPathInfo();
        Mockito.doReturn(Boolean.TRUE).when(mockProductService).delete(Mockito.anyLong());

        productServlet.doDelete(mockRequest, mockResponse);

        Mockito.verify(mockProductService).delete(Mockito.anyLong());
        Mockito.verify(mockResponse).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    void doDeleteBadRequest() throws ServletException, IOException {
        Mockito.doReturn("product/a200").when(mockRequest).getPathInfo();

        productServlet.doDelete(mockRequest, mockResponse);

        Mockito.verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }
}
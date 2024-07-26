package ru.aston.rest_service.service.impl;

import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import ru.aston.rest_service.exception.NotFoundException;
import ru.aston.rest_service.model.Product;
import ru.aston.rest_service.repository.OrderDetailDao;
import ru.aston.rest_service.repository.ProductCategoryDao;
import ru.aston.rest_service.repository.ProductDao;
import ru.aston.rest_service.repository.impl.OrderDetailDaoImpl;
import ru.aston.rest_service.repository.impl.ProductCategoryDaoImpl;
import ru.aston.rest_service.repository.impl.ProductDaoImpl;
import ru.aston.rest_service.service.ProductService;
import org.mockito.Mockito;
import ru.aston.rest_service.servlet.dto.product.ProductDtoOut;
import ru.aston.rest_service.servlet.dto.product.ProductNewDtoIn;
import ru.aston.rest_service.servlet.dto.product.ProductUpdateDtoIn;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

class ProductServiceImplTest {
    private static ProductService productService;
    private static ProductDao mockProductRepository;
    private static ProductCategoryDao mockProductCategoryRepository;
    private static OrderDetailDao mockOrderDetailRepository;
    private static ProductDaoImpl productDaoInstance;
    private static ProductCategoryDaoImpl productCategoryDaoInstance;
    private static OrderDetailDaoImpl orderDetailDaoInstance;

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

    private static void setMock(ProductCategoryDao mock) {
        try {
            Field instance = ProductCategoryDaoImpl.class.getDeclaredField("productCategoryRepository");
            instance.setAccessible(true);
            productCategoryDaoInstance = (ProductCategoryDaoImpl) instance.get(instance);
            instance.set(instance, mock);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

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

    @BeforeAll
    static void beforeAll() {
        mockProductRepository = Mockito.mock(ProductDao.class);
        setMock(mockProductRepository);
        mockProductCategoryRepository = Mockito.mock(ProductCategoryDao.class);
        setMock(mockProductCategoryRepository);
        mockOrderDetailRepository = Mockito.mock(OrderDetailDao.class);
        setMock(mockOrderDetailRepository);

        productService = ProductServiceImpl.getInstance();
    }

    @AfterAll
    static void afterAll() throws Exception {
        Field instance = ProductDaoImpl.class.getDeclaredField("productRepository");
        instance.setAccessible(true);
        instance.set(instance, productDaoInstance);

        instance = ProductCategoryDaoImpl.class.getDeclaredField("productCategoryRepository");
        instance.setAccessible(true);
        instance.set(instance, productCategoryDaoInstance);

        instance = OrderDetailDaoImpl.class.getDeclaredField("orderDetailRepository");
        instance.setAccessible(true);
        instance.set(instance, orderDetailDaoInstance);
    }

    @BeforeEach
    void setUp() {
        Mockito.reset(mockProductRepository);
    }

    @Test
    void save() {
        Long id = 1L;
        String expectedName = "test";

        ProductNewDtoIn dto = new ProductNewDtoIn("Product #1", List.of());
        Product product = new Product(id, "test");

        Mockito.doReturn(product).when(mockProductRepository).save(Mockito.any(Product.class));

        ProductDtoOut result = productService.save(dto);

        Assertions.assertEquals(expectedName, result.getName());
    }

    @Test
    void update() throws NotFoundException {
        Long expectedId = 1L;

        ProductUpdateDtoIn dto = new ProductUpdateDtoIn(expectedId, "product update #1");

        Mockito.doReturn(true).when(mockProductRepository).exitsById(Mockito.anyLong());

        productService.update(dto);

        ArgumentCaptor<Product> argument = ArgumentCaptor.forClass(Product.class);
        Mockito.verify(mockProductRepository).update(argument.capture());

        Product result = argument.getValue();
        Assertions.assertEquals(expectedId, result.getId());
    }

    @Test
    void updateNotFound() {
        Long expectedId = 1L;

        ProductUpdateDtoIn dto = new ProductUpdateDtoIn(expectedId, "product update #1");

        Mockito.doReturn(false).when(mockProductRepository).exitsById(Mockito.anyLong());

        NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> productService.update(dto), "Not found."
        );
        Assertions.assertEquals("Продукт с таким id = " + expectedId + " не найден.", exception.getMessage());
    }

    @Test
    void findById() throws NotFoundException {
        Long id = 1L;
        String expectedName = "product found #1";

        Optional<Product> product = Optional.of(new Product(id, expectedName));

        Mockito.doReturn(true).when(mockProductRepository).exitsById(Mockito.anyLong());
        Mockito.doReturn(product).when(mockProductRepository).findById(Mockito.anyLong());

        ProductDtoOut dto = productService.findById(id);

        Assertions.assertEquals(expectedName, dto.getName());
    }

    @Test
    void findByIdNotFound() {
        Long expectedId = 1L;

        Mockito.doReturn(false).when(mockProductRepository).exitsById(Mockito.anyLong());

        NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> productService.findById(expectedId), "Not found."
        );
        Assertions.assertEquals("Продукт с таким id = " + expectedId + " не найден.", exception.getMessage());
    }

    @Test
    void findAll() {
        productService.findAll();
        Mockito.verify(mockProductRepository).findAll();
    }

    @Test
    void delete() throws NotFoundException {
        Long expectedId = 1L;

        Mockito.doReturn(true).when(mockProductRepository).exitsById(Mockito.anyLong());
        productService.delete(expectedId);

        ArgumentCaptor<Long> argument = ArgumentCaptor.forClass(Long.class);
        Mockito.verify(mockProductRepository).deleteById(argument.capture());

        Long result = argument.getValue();
        Assertions.assertEquals(expectedId, result);
    }
}
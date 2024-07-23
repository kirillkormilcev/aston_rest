package ru.aston.rest_service.repository.impl;

import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.ext.ScriptUtils;
import org.testcontainers.jdbc.JdbcDatabaseDelegate;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.aston.rest_service.model.Product;
import ru.aston.rest_service.repository.ProductDao;
import ru.aston.rest_service.servlet.dto.product.ProductNewDtoIn;
import ru.aston.rest_service.servlet.mapper.ProductMapper;
import ru.aston.rest_service.utility.PropertiesUtil;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Testcontainers
@Tag("DockerRequired")
class ProductDaoImplTest {
    private static final String INIT_SQL = "schema.sql";
    private static final String INIT_DATA = "data.sql";
    public static ProductDao productRepository;
    private static int containerPort = 5432;
    private static int localPort = 6544;
    @Container
    public static PostgreSQLContainer<?> container = new PostgreSQLContainer<>("postgres:14.4-alpine")
            .withDatabaseName(PropertiesUtil.getProperties("db.name"))
            .withUsername(PropertiesUtil.getProperties("db.username"))
            .withPassword(PropertiesUtil.getProperties("db.password"))
            .withExposedPorts(containerPort)
            .withCreateContainerCmdModifier(cmd -> cmd.withHostConfig(
                    new HostConfig().withPortBindings(new PortBinding(Ports.Binding.bindPort(localPort),
                            new ExposedPort(containerPort)))
            ))
            .withInitScript(INIT_SQL);
    private static JdbcDatabaseDelegate jdbcDatabaseDelegate;
    private static final ProductMapper productMapper = ProductMapper.INSTANCE;

    @BeforeAll
    static void beforeAll() {
        container.start();
        productRepository = ProductDaoImpl.getProductRepository();
        jdbcDatabaseDelegate = new JdbcDatabaseDelegate(container, "");
    }

    @AfterAll
    static void afterAll() {
        container.stop();
    }

    @BeforeEach
    void setUp() {
        ScriptUtils.runInitScript(jdbcDatabaseDelegate, INIT_SQL);
        ScriptUtils.runInitScript(jdbcDatabaseDelegate, INIT_DATA);
    }

    @Test
    void save() {
        String expectedName = "Rum";

        ProductNewDtoIn productNewDtoIn = new ProductNewDtoIn(
                expectedName,
                BigDecimal.valueOf(300),
                12,
                true,
                List.of(4L, 5L)
        );
        Product product = productRepository.save(productMapper.productNewDtoInToProduct(productNewDtoIn));
        Optional<Product> result = productRepository.findById(product.getId());

        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(expectedName, result.get().getName());
    }

    @Test
    void update() {
        String expectedName = "Gin new";

        Product product = productRepository.findById(2L).get();
        String oldName = product.getName();
        product.setName(expectedName);
        productRepository.update(product);

        Product result = productRepository.findById(2L).get();

        Assertions.assertNotEquals(result.getName(), oldName);
        Assertions.assertEquals(expectedName, result.getName());
    }

    @Test
    void deleteById() {
        Boolean expectedDeleted = true;

        Boolean result = productRepository.deleteById(4L);

        Assertions.assertEquals(expectedDeleted, result);
    }

    @Test
    void findById() {
        String expectedName = "Gin";
        Product product = productRepository.findById(2L).get();

        Assertions.assertEquals(expectedName, product.getName());
    }

    @Test
    void findByIds() {
        int expectedSize = 2;
        List<Product> products = productRepository.findByIds(List.of(2L, 3L));

        Assertions.assertEquals(expectedSize, products.size());
    }

    @Test
    void findAll() {
        int expectedSize = 5;

        List<Product> products = productRepository.findAll();
        Assertions.assertEquals(expectedSize, products.size());
    }

    @Test
    void findAllIds() {
        int expectedSize = 5;

        List<Long> productIds = productRepository.findAllIds();

        Assertions.assertEquals(expectedSize, productIds.size());
    }

    @Test
    void exitsById() {
        Boolean expectedExist = true;

        Boolean result = productRepository.exitsById(2L);

        Assertions.assertEquals(expectedExist, result);
    }
}
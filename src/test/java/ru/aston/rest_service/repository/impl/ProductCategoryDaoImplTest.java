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
import ru.aston.rest_service.model.ProductCategory;
import ru.aston.rest_service.repository.ProductCategoryDao;
import ru.aston.rest_service.utility.PropertiesUtil;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@Tag("DockerRequired")
class ProductCategoryDaoImplTest {
    private static final String INIT_SQL = "schema.sql";
    private static final String INIT_DATA = "data.sql";
    public static ProductCategoryDao productCategoryRepository;
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

    @BeforeAll
    static void beforeAll() {
        container.start();
        productCategoryRepository = ProductCategoryDaoImpl.getProductCategoryRepository();
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
    void findByIds() {
        int expectedSize = 2;

        List<ProductCategory> productCategories = productCategoryRepository.findByIds(List.of(1L, 2L));

        assertEquals(expectedSize, productCategories.size());
    }

    @Test
    void findAllIds() {
        int expectedSize = 6;

        List<Long> ids = productCategoryRepository.findAllIds();

        Assertions.assertEquals(expectedSize, ids.size());
    }
}
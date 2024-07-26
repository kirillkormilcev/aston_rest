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
import ru.aston.rest_service.model.OrderDetail;
import ru.aston.rest_service.model.OrderStatus;
import ru.aston.rest_service.repository.OrderDetailDao;
import ru.aston.rest_service.servlet.dto.order_detail.OrderDetailNewDtoIn;
import ru.aston.rest_service.servlet.dto.order_detail.OrderDetailUpdateDtoIn;
import ru.aston.rest_service.servlet.mapper.OrderDetailMapper;
import ru.aston.rest_service.utility.PropertiesUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Testcontainers
@Tag("DockerRequired")
class OrderDetailDaoImplTest {
    private static final String INIT_SQL = "schema.sql";
    private static final String INIT_DATA = "data.sql";
    public static OrderDetailDao orderDetailDaoRepository;
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
    private static final OrderDetailMapper orderDetailMapper = OrderDetailMapper.INSTANCE;

    @BeforeAll
    static void beforeAll() {
        container.start();
        orderDetailDaoRepository = OrderDetailDaoImpl.getOrderDetailRepository();
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
        OrderStatus expectedStatus = OrderStatus.CREATED;

        OrderDetailNewDtoIn orderDetailNewDtoIn = new OrderDetailNewDtoIn(
                OrderStatus.CREATED,
                List.of(1L, 3L)
        );
        OrderDetail orderDetail = orderDetailDaoRepository
                .save(orderDetailMapper.orderDetailNewDtoInToOrderDetail(orderDetailNewDtoIn));
        Optional<OrderDetail> result = orderDetailDaoRepository.findById(orderDetail.getId());

        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(expectedStatus, result.get().getStatus());
    }

    @Test
    void update() {
        OrderStatus expectedStatus = OrderStatus.PAID;

        OrderDetail orderDetail = orderDetailDaoRepository.findById(1L).get();
        OrderStatus oldStatus = orderDetail.getStatus();

        OrderDetailUpdateDtoIn orderDetailUpdateDtoIn = new OrderDetailUpdateDtoIn(
                1L,
                expectedStatus,
                List.of(1L, 3L)
        );
        orderDetailDaoRepository.update(orderDetailMapper.orderDetailUpdateDtoInToOrderDetail(orderDetailUpdateDtoIn));

        Optional<OrderDetail> result = orderDetailDaoRepository.findById(orderDetailUpdateDtoIn.getId());

        Assertions.assertNotEquals(oldStatus, result.get().getStatus());
        Assertions.assertEquals(expectedStatus, result.get().getStatus());
    }

    @Test
    void deleteById() {
        Boolean expectedDeleted = true;

        Boolean result = orderDetailDaoRepository.deleteById(1L);

        Assertions.assertEquals(expectedDeleted, result);
    }

    @Test
    void findById() {
        ArrayList<Long> expectedList = new ArrayList<>(List.of(1L, 3L));

        OrderDetail orderDetail = orderDetailDaoRepository.findById(1L).get();

        Assertions.assertEquals(orderDetail.getProductIds(), expectedList);
    }

    @Test
    void findAll() {
        int expectedSize = 2;

        List<OrderDetail> orderDetailList = orderDetailDaoRepository.findAll();

        Assertions.assertEquals(expectedSize, orderDetailList.size());
    }

    @Test
    void findAllWithCondition() {
        int expectedSize = 1;

        List<OrderDetail> orderDetailList = orderDetailDaoRepository.findAllWithCondition("PAID");

        Assertions.assertEquals(expectedSize, orderDetailList.size());
    }

    @Test
    void exitsById() {
        Boolean expectedExist = true;

        Boolean result = orderDetailDaoRepository.exitsById(1L);

        Assertions.assertEquals(expectedExist, result);
    }

    @Test
    void existProductInOrderDetailsByID() {
        Boolean expectedExistProductInOrder = true;

        Boolean result = orderDetailDaoRepository.existProductInOrderDetailsByID(1L);

        Assertions.assertEquals(expectedExistProductInOrder, result);
    }
}
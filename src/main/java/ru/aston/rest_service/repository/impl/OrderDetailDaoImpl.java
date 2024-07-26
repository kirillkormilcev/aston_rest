package ru.aston.rest_service.repository.impl;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.aston.rest_service.db.ConnectionManager;
import ru.aston.rest_service.db.ConnectionManagerImpl;
import ru.aston.rest_service.exception.DataBaseStatementException;
import ru.aston.rest_service.model.OrderDetail;
import ru.aston.rest_service.model.OrderStatus;
import ru.aston.rest_service.repository.OrderDetailDao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderDetailDaoImpl implements OrderDetailDao {

    private static final String SAVE_SQL = """
            INSERT INTO order_details (order_status, total_amount)
            VALUES (?, ?);
            """;
    private static final String UPDATE_SQL = """
            UPDATE order_details
            SET order_status = ?,
                total_amount = ?
            WHERE id = ?  ;
            """;
    private static final String DELETE_PRODUCT_RELATIONS_SQL = """
            DELETE FROM order_details_products
            WHERE order_detail_id = ? ;
            """;
    private static final String SAVE_PRODUCT_RELATIONS_SQL = """
            INSERT INTO order_details_products (order_detail_id, product_id)
            VALUES (?, ?);
            """;
    private static final String EXIST_BY_ID_SQL = """
            SELECT exists (
                SELECT 1
                FROM order_details
                WHERE id = ?
                LIMIT 1);
            """;
    private static final String FIND_ALL_SQL = """
            SELECT id, order_status, total_amount  FROM order_details;
            """;
    private static final String FIND_ALL_CONDITION_SQL = """
            SELECT id, order_status, total_amount  FROM order_details
            WHERE order_status = ?;
            """;
    private static final String FIND_BY_ID_SQL = """
            SELECT id, order_status, total_amount, product_id FROM order_details od
            JOIN order_details_products odp on od.id = odp.order_detail_id
            WHERE od.id = ?;
            """;
    private static final String EXIST_PRODUCT_BY_ID_SQL = """
            SELECT exists (
                SELECT 1
                FROM order_details_products
                WHERE product_id = ?
                LIMIT 1);
            """;
    private static final String DELETE_SQL = """
            DELETE FROM db.public.order_details
            WHERE id = ?;
            """;

    private static OrderDetailDao orderDetailRepository;
    private final ConnectionManager connectionManager = ConnectionManagerImpl.getInstance();

    public static synchronized OrderDetailDao getOrderDetailRepository() {
        if (orderDetailRepository == null) {
            orderDetailRepository = new OrderDetailDaoImpl();
        }
        return orderDetailRepository;
    }

    @Override
    public OrderDetail save(OrderDetail orderDetail) {
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, orderDetail.getStatus().name());
            preparedStatement.setBigDecimal(2, orderDetail.getTotalAmount());
            preparedStatement.executeUpdate();

            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                orderDetail = new OrderDetail(
                        resultSet.getLong("id"),
                        OrderStatus.valueOf(resultSet.getString("order_status")),
                        orderDetail.getProductIds(),
                        resultSet.getBigDecimal("total_amount")
                );
            }
            updateProductsRelations(orderDetail);
        } catch (SQLException e) {
            throw new DataBaseStatementException("Ошибка при сохранении заказа в базе данных."
                    + System.lineSeparator() + e.getMessage());
        }
        return orderDetail;
    }

    @Override
    public void update(OrderDetail orderDetail) {
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_SQL)) {

            preparedStatement.setString(1, orderDetail.getStatus().name());
            preparedStatement.setBigDecimal(2, orderDetail.getTotalAmount());
            preparedStatement.setLong(3, orderDetail.getId());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataBaseStatementException("Ошибка при изменении заказа в базе данных."
                    + System.lineSeparator() + e.getMessage());
        }
        updateProductsRelations(orderDetail);
    }

    @Override
    public boolean deleteById(Long id) {
        boolean deleteResult;
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_SQL)) {
            preparedStatement.setLong(1, id);
            deleteResult = preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataBaseStatementException("Ошибка при удалении заказа в базе данных."
                    + System.lineSeparator() + e.getMessage());
        }
        return deleteResult;
    }

    @Override
    public Optional<OrderDetail> findById(Long id) {
        OrderDetail orderDetail;
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            orderDetail = createOrderDetailWithoutProducts(resultSet);
            List<Long> productIds = orderDetail.getProductIds();
            do {
                productIds.add(resultSet.getLong("product_id"));
            } while (resultSet.next());
            orderDetail.setProductIds(productIds);
        } catch (SQLException e) {
            throw new DataBaseStatementException("Ошибка при поиске заказа по id."
                    + System.lineSeparator() + e.getMessage());
        }
        return Optional.of(orderDetail);
    }

    @Override
    public List<OrderDetail> findAll() {
        List<OrderDetail> orderDetails = new ArrayList<>();
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_SQL)) {

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                orderDetails.add(createOrderDetailWithoutProducts(resultSet));
            }
        } catch (SQLException e) {
            throw new DataBaseStatementException("Ошибка при поиске всех заказов."
                    + System.lineSeparator() + e.getMessage());
        }
        return orderDetails;
    }

    @Override
    public List<OrderDetail> findAllWithCondition(String condition) {
        List<OrderDetail> orderDetails = new ArrayList<>();
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_CONDITION_SQL)) {

            preparedStatement.setString(1, condition);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                orderDetails.add(createOrderDetailWithoutProducts(resultSet));
            }
        } catch (SQLException e) {
            throw new DataBaseStatementException("Ошибка при поиске всех заказов с условием."
                    + System.lineSeparator() + e.getMessage());
        }
        return orderDetails;
    }

    @Override
    public boolean exitsById(Long id) {
        boolean isExists = false;
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(EXIST_BY_ID_SQL)) {

            preparedStatement.setLong(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                isExists = resultSet.getBoolean(1);
            }
        } catch (SQLException e) {
            throw new DataBaseStatementException("Ошибка при поиске id заказа в базе данных."
                    + System.lineSeparator() + e.getMessage());
        }
        return isExists;
    }

    @Override
    public boolean existProductInOrderDetailsByID(Long productId) {
        boolean isExists = false;
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(EXIST_PRODUCT_BY_ID_SQL)) {

            preparedStatement.setLong(1, productId);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                isExists = resultSet.getBoolean(1);
            }
        } catch (SQLException e) {
            throw new DataBaseStatementException("Ошибка при поиске id продукта среди заказов в базе данных."
                    + System.lineSeparator() + e.getMessage());
        }
        return isExists;
    }

    private void updateProductsRelations(OrderDetail orderDetail) {
        deleteProductRelations(orderDetail.getId());
        insertProductRelations(orderDetail);
    }

    private void deleteProductRelations(Long id) {
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_PRODUCT_RELATIONS_SQL)) {
            preparedStatement.setLong(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataBaseStatementException("Ошибка при удалении связей заказа с продуктом."
                    + System.lineSeparator() + e.getMessage());
        }
    }

    private void insertProductRelations(OrderDetail orderDetail) {
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SAVE_PRODUCT_RELATIONS_SQL)) {
            Long orderDetailId = orderDetail.getId();
            List<Long> productIds = orderDetail.getProductIds();
            preparedStatement.setLong(1, orderDetailId);
            for (Long productId : productIds) {
                preparedStatement.setLong(2, productId);
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
        } catch (SQLException e) {
            throw new DataBaseStatementException("Ошибка при записи связей заказа с продуктами."
                    + System.lineSeparator() + e.getMessage());
        }
    }

    private OrderDetail createOrderDetailWithoutProducts(ResultSet resultSet) throws SQLException {
        return new OrderDetail(
                resultSet.getLong("id"),
                OrderStatus.valueOf(resultSet.getString("order_status")),
                new ArrayList<>(),
                resultSet.getBigDecimal("total_amount")
        );
    }
}
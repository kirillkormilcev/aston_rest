package ru.aston.rest_service.repository.impl;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.aston.rest_service.db.ConnectionManager;
import ru.aston.rest_service.db.ConnectionManagerImpl;
import ru.aston.rest_service.exception.DataBaseStatementException;
import ru.aston.rest_service.model.Product;
import ru.aston.rest_service.repository.ProductDao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductDaoImpl implements ProductDao {

    private static final String SAVE_SQL = """
            INSERT INTO products (name, price, quantity, available)
            VALUES (?, ? ,?, ?);
            """;
    private static final String UPDATE_SQL = """
            UPDATE products
            SET name = ?,
                price = ?,
                quantity = ?,
                available = ?
            WHERE id = ?  ;
            """;
    private static final String DELETE_PRODUCT_RELATIONS_SQL = """
            DELETE FROM products_product_categories
            WHERE product_id = ? ;
            """;
    private static final String SAVE_PRODUCT_RELATIONS_SQL = """
            INSERT INTO products_product_categories (product_id, product_category_id)
            VALUES (?, ?);
            """;
    private static final String FIND_ALL_SQL = """
            SELECT id, name, price, quantity, available  FROM products
            WHERE available = true;
            """;
    private static final String EXIST_BY_ID_SQL = """
            SELECT exists (
                SELECT 1
                FROM products
                WHERE id = ?
                LIMIT 1);
            """;
    private static final String FIND_BY_ID_SQL = """
            SELECT id, name, price, quantity, available, product_category_id FROM products
            JOIN products_product_categories ppc on products.id = ppc.product_id
            WHERE id = ?;
            """;
    private static final String DELETE_SQL = """
            DELETE FROM products
            WHERE id = ?;
            """;
    private static final String FIND_ALL_AVAILABLE_IDS_SQL = """
            SELECT id FROM products
            WHERE available = true;
            """;
    private static final String FIND_BY_IDS_SQL = """
            SELECT id, name, price, quantity, available FROM products
            WHERE id = ANY(?);
            """;

    private static ProductDao productRepository;
    private final ConnectionManager connectionManager = ConnectionManagerImpl.getInstance();

    public static synchronized ProductDao getProductRepository() {
        if (productRepository == null) {
            productRepository = new ProductDaoImpl();
        }
        return productRepository;
    }

    @Override
    public Product save(Product product) {
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, product.getName());
            preparedStatement.setBigDecimal(2, product.getPrice());
            preparedStatement.setInt(3, product.getQuantity());
            preparedStatement.setBoolean(4, product.isAvailable());
            preparedStatement.executeUpdate();

            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                product = new Product(
                        resultSet.getLong("id"),
                        resultSet.getString("name"),
                        resultSet.getBigDecimal("price"),
                        resultSet.getInt("quantity"),
                        resultSet.getBoolean("available"),
                        product.getProductCategoryIds()
                );
            }
            updateProductCategoriesListRelations(product);
        } catch (SQLException e) {
            throw new DataBaseStatementException("Ошибка при сохранении  продукта."
                    + System.lineSeparator() + e.getMessage());
        }
        return product;
    }

    @Override
    public void update(Product product) {
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_SQL)) {

            preparedStatement.setString(1, product.getName());
            preparedStatement.setBigDecimal(2, product.getPrice());
            preparedStatement.setInt(3, product.getQuantity());
            preparedStatement.setBoolean(4, product.isAvailable());
            preparedStatement.setLong(5, product.getId());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataBaseStatementException("Ошибка при изменении продукта в базе данных."
                    + System.lineSeparator() + e.getMessage());
        }
        updateProductCategoriesListRelations(product);
    }

    @Override
    public boolean deleteById(Long id) {
        boolean deleteResult;
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_SQL)) {
            preparedStatement.setLong(1, id);
            deleteResult = preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataBaseStatementException("Ошибка при удалении продукта в базе данных."
                    + System.lineSeparator() + e.getMessage());
        }
        return deleteResult;
    }

    @Override
    public Optional<Product> findById(Long id) {
        Product product;
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            product = createProductWithoutCategories(resultSet);
            List<Long> productCategoriesList = product.getProductCategoryIds();
            do {
                productCategoriesList.add(resultSet.getLong("product_category_id"));
            } while (resultSet.next());
            product.setProductCategoryIds(productCategoriesList);
        } catch (SQLException e) {
            throw new DataBaseStatementException("Ошибка при поиске продукта по id."
                    + System.lineSeparator() + e.getMessage());
        }
        return Optional.of(product);
    }

    @Override
    public List<Product> findByIds(List<Long> ids) {
        List<Product> products = new ArrayList<>();
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_IDS_SQL)) {
            preparedStatement.setArray(1, connection.createArrayOf("LONG", ids.toArray()));
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                products.add(createProductWithoutCategories(resultSet));
            }
        } catch (SQLException e) {
            throw new DataBaseStatementException("Ошибка при поиске продукта по id."
                    + System.lineSeparator() + e.getMessage());
        }
        return products;
    }

    @Override
    public List<Product> findAll() {
        List<Product> products = new ArrayList<>();
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_SQL)) {

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                products.add(createProductWithoutCategories(resultSet));
            }
        } catch (SQLException e) {
            throw new DataBaseStatementException("Ошибка при поиске всех продуктов."
                    + System.lineSeparator() + e.getMessage());
        }
        return products;
    }

    @Override
    public List<Long> findAllIds() {
        List<Long> idsList = new ArrayList<>();
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_AVAILABLE_IDS_SQL)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                idsList.add(resultSet.getLong(1));
            }
        } catch (SQLException e) {
            throw new DataBaseStatementException("Ошибка при поиске всех id продуктов."
                    + System.lineSeparator() + e.getMessage());
        }
        return idsList;
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
            throw new DataBaseStatementException("Ошибка при поиске id продукта в базе данных."
                    + System.lineSeparator() + e.getMessage());
        }
        return isExists;
    }

    private Product createProductWithoutCategories(ResultSet resultSet) throws SQLException {
        return new Product(
                resultSet.getLong("id"),
                resultSet.getString("name"),
                resultSet.getBigDecimal("price"),
                resultSet.getInt("quantity"),
                resultSet.getBoolean("available"),
                new ArrayList<>()
        );
    }

    private void updateProductCategoriesListRelations(Product product) {
        deleteProductCategoriesListRelations(product.getId());
        insertProductCategoriesListRelations(product);
    }
    private void deleteProductCategoriesListRelations(Long id) {
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_PRODUCT_RELATIONS_SQL)) {
            preparedStatement.setLong(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataBaseStatementException("Ошибка при удалении категорий соответствующих продукту."
                    + System.lineSeparator() + e.getMessage());
        }
    }

    private void insertProductCategoriesListRelations(Product product) {
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SAVE_PRODUCT_RELATIONS_SQL)) {
            Long productId = product.getId();
            List<Long> productCategoryIds = product.getProductCategoryIds();
            preparedStatement.setLong(1, productId);
            for (Long productCategoryId : productCategoryIds) {
                preparedStatement.setLong(2, productCategoryId);
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
        } catch (SQLException e) {
            throw new DataBaseStatementException("Ошибка при записи категорий соответствующих продукту."
                    + System.lineSeparator() + e.getMessage());
        }
    }
}

package ru.aston.rest_service.repository.impl;

import ru.aston.rest_service.db.ConnectionManager;
import ru.aston.rest_service.db.ConnectionManagerImpl;
import ru.aston.rest_service.exception.DataBaseStatementException;
import ru.aston.rest_service.model.CategoryType;
import ru.aston.rest_service.model.ProductCategory;
import ru.aston.rest_service.repository.ProductCategoryDao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProductCategoryDaoImpl implements ProductCategoryDao {
    private static final String FIND_ALL_IDS_SQL = """
            SELECT id FROM product_categories;
            """;

    private static final String FIND_BY_IDS_SQL = """
            SELECT id, name, category_type FROM product_categories
            WHERE id = ANY(?);
            """;

    private static ProductCategoryDao productCategoryRepository;
    private final ConnectionManager connectionManager = ConnectionManagerImpl.getInstance();

    public static synchronized ProductCategoryDao getProductCategoryRepository() {
        if (productCategoryRepository == null) {
            productCategoryRepository = new ProductCategoryDaoImpl();
        }
        return productCategoryRepository;
    }

    /**
     * Метод не реализован
     * реализация сохранена
     * для будущего развития
     */
    @Override
    public ProductCategory save(ProductCategory productCategory) {
        return null;
    }

    /**
     * Метод не реализован
     * реализация сохранена
     * для будущего развития
     */
    @Override
    public void update(ProductCategory productCategory) {
        // смотри комментарий к методу
    }

    /**
     * Метод не реализован
     * реализация сохранена
     * для будущего развития
     */
    @Override
    public boolean deleteById(Long id) {
        return false;
    }

    /**
     * Метод не реализован
     * реализация сохранена
     * для будущего развития
     */
    @Override
    public Optional<ProductCategory> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public List<ProductCategory> findByIds(List<Long> ids) {
        List<ProductCategory> productCategoryList = new ArrayList<>();
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_IDS_SQL)) {
            preparedStatement.setArray(1, connection.createArrayOf("LONG", ids.toArray()));
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                productCategoryList.add(new ProductCategory(resultSet.getLong("id"),
                        resultSet.getString("name"),
                        CategoryType.valueOf(resultSet.getString("category_type"))));
            }
        } catch (SQLException e) {
            throw new DataBaseStatementException("Ошибка при поиске категории продута по id."
                    + System.lineSeparator() + e.getMessage());
        }
        return productCategoryList;
    }

    @Override
    public List<Long> findAllIds() {
        List<Long> idsList = new ArrayList<>();
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_IDS_SQL)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                idsList.add(resultSet.getLong(1));
            }
        } catch (SQLException e) {
            throw new DataBaseStatementException("Ошибка при поиске всех id категорий продуктов."
                    + System.lineSeparator() + e.getMessage());
        }
        return idsList;
    }
}
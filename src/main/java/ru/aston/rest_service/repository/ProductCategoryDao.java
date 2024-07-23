package ru.aston.rest_service.repository;

import ru.aston.rest_service.model.ProductCategory;

import java.util.List;

public interface ProductCategoryDao extends Dao<ProductCategory, Long> {

    List<ProductCategory> findByIds(List<Long> ids);

    List<Long> findAllIds();
}

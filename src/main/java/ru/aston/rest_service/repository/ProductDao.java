package ru.aston.rest_service.repository;

import ru.aston.rest_service.model.Product;

import java.util.List;

public interface ProductDao extends Dao<Product, Long> {

    List<Product> findByIds(List<Long> ids);

    List<Product> findAll();

    List<Long> findAllIds();

    boolean exitsById(Long id);
}
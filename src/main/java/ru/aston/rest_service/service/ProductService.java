package ru.aston.rest_service.service;

import ru.aston.rest_service.servlet.dto.product.ProductDtoOut;
import ru.aston.rest_service.servlet.dto.product.ProductNewDtoIn;
import ru.aston.rest_service.exception.NotFoundException;
import ru.aston.rest_service.servlet.dto.product.ProductUpdateDtoIn;

import java.util.List;

public interface ProductService {

    ProductDtoOut save(ProductNewDtoIn productDto);

    void update(ProductUpdateDtoIn productUpdateDtoIn) throws NotFoundException;

    ProductDtoOut findById(Long productId) throws NotFoundException;

    List<ProductDtoOut> findAll();

    Boolean delete(Long productId) throws NotFoundException;
}
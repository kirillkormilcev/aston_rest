package ru.aston.rest_service.service.impl;

import lombok.NoArgsConstructor;
import ru.aston.rest_service.exception.NotFoundException;
import ru.aston.rest_service.exception.ValidationException;
import ru.aston.rest_service.model.Product;
import ru.aston.rest_service.repository.OrderDetailDao;
import ru.aston.rest_service.repository.ProductCategoryDao;
import ru.aston.rest_service.repository.ProductDao;
import ru.aston.rest_service.repository.impl.OrderDetailDaoImpl;
import ru.aston.rest_service.repository.impl.ProductCategoryDaoImpl;
import ru.aston.rest_service.repository.impl.ProductDaoImpl;
import ru.aston.rest_service.service.ProductService;
import ru.aston.rest_service.servlet.dto.product.ProductDtoOut;
import ru.aston.rest_service.servlet.dto.product.ProductNewDtoIn;
import ru.aston.rest_service.servlet.dto.product.ProductUpdateDtoIn;
import ru.aston.rest_service.servlet.mapper.ProductMapper;

import java.util.HashSet;
import java.util.List;

@NoArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductDao productRepository = ProductDaoImpl.getProductRepository();
    private final ProductCategoryDao productCategoryRepository = ProductCategoryDaoImpl.getProductCategoryRepository();
    private final OrderDetailDao orderDetailRepository = OrderDetailDaoImpl.getOrderDetailRepository();
    private static final ProductMapper productMapper = ProductMapper.INSTANCE;
    private static ProductService productService;

    public static synchronized ProductService getInstance() {
        if (productService == null) {
            productService = new ProductServiceImpl();
        }
        return productService;
    }

    @Override
    public ProductDtoOut save(ProductNewDtoIn productNewDtoIn) {
        validationProductDto(productNewDtoIn);
        Product product = productRepository.save(productMapper.productNewDtoInToProduct(productNewDtoIn));
        return productMapper.productToProductDtoOut(product);
    }

    @Override
    public void update(ProductUpdateDtoIn productUpdateDtoIn) throws NotFoundException {
        if (productUpdateDtoIn == null || productUpdateDtoIn.getId() == null) {
            throw new IllegalArgumentException();
        }
        checkProductId(productUpdateDtoIn.getId());
        productRepository.update(productMapper.productUpdateDtoInToProduct(productUpdateDtoIn));
    }

    @Override
    public ProductDtoOut findById(Long productId) throws NotFoundException {
        checkProductId(productId);
        return productMapper.productToProductDtoOut(productRepository.findById(productId).orElseThrow());
    }

    @Override
    public List<ProductDtoOut> findAll() {
        List<Product> products = productRepository.findAll();
        return productMapper.productListToProductDtoOutList(products);
    }

    @Override
    public Boolean delete(Long productId) throws NotFoundException {
        checkProductId(productId);
        checkExistProductInOrderDetails(productId);
        return productRepository.deleteById(productId);
    }

    private void validationProductDto(ProductNewDtoIn productNewDtoIn) {
        if (!new HashSet<>(productCategoryRepository.findAllIds()).containsAll(productNewDtoIn.getProductCategoryIds())) {
            throw new ValidationException("Таких категорий продукта еще нет, создайте их сначала.");
        }
    }

    private void checkProductId(Long productId) throws NotFoundException {
        if (!productRepository.exitsById(productId)) {
            throw new NotFoundException("Продукт с таким id = " + productId + " не найден.");
        }
    }

    private void checkExistProductInOrderDetails(Long productId) throws NotFoundException {
        if (orderDetailRepository.existProductInOrderDetailsByID(productId)) {
            throw new NotFoundException("Продукт с таким id = " + productId + " содержится в заказах.");
        }
    }
}
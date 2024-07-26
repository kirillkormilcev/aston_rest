package ru.aston.rest_service.servlet.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.aston.rest_service.model.Product;
import ru.aston.rest_service.model.ProductCategory;
import ru.aston.rest_service.repository.ProductCategoryDao;
import ru.aston.rest_service.repository.impl.ProductCategoryDaoImpl;
import ru.aston.rest_service.servlet.dto.product.*;

import java.util.List;

@Mapper
public interface ProductMapper {

    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    ProductCategoryDao productCategoryRepository = ProductCategoryDaoImpl.getProductCategoryRepository();

    @Mapping(target = "productCategories", expression = "java(productCategoryIdsToProductCategoryDtoOutList(product.getProductCategoryIds()))")
    ProductDtoOut productToProductDtoOut(Product product);

    default List<ProductCategoryDtoOut> productCategoryIdsToProductCategoryDtoOutList(List<Long> productCategoryIds) {
        if (productCategoryIds == null || productCategoryIds.isEmpty()) {
            return List.of();
        }
        return INSTANCE.productCategoryToProductCategoryDtoOutList(productCategoryRepository.findByIds(productCategoryIds));
    }

    @Mapping(target = "productCategories", source = "productCategoryIds", ignore = true)
    List<ProductDtoOut> productListToProductDtoOutList(List<Product> products);

    @Mapping(target = "productCategories", source = "productCategoryIds", ignore = true)
    List<ProductShortDtoOut> productListToProductShortDtoOutList(List<Product> products);

    ProductCategoryDtoOut productCategoryToProductCategoryDtoOut(ProductCategory productCategory);

    @Mapping(target = "id", ignore = true)
    List<ProductCategoryDtoOut> productCategoryToProductCategoryDtoOutList(List<ProductCategory> productCategoryList);

    @Mapping(target = "id", ignore = true)
    Product productNewDtoInToProduct(ProductNewDtoIn productNewDtoIn);

    Product productUpdateDtoInToProduct(ProductUpdateDtoIn productUpdateDtoIn);
}
package ru.aston.rest_service.servlet.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.aston.rest_service.model.OrderDetail;
import ru.aston.rest_service.model.Product;
import ru.aston.rest_service.repository.ProductDao;
import ru.aston.rest_service.repository.impl.ProductDaoImpl;
import ru.aston.rest_service.servlet.dto.order_detail.OrderDetailDtoOut;
import ru.aston.rest_service.servlet.dto.order_detail.OrderDetailNewDtoIn;
import ru.aston.rest_service.servlet.dto.order_detail.OrderDetailUpdateDtoIn;
import ru.aston.rest_service.servlet.dto.product.ProductShortDtoOut;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface OrderDetailMapper {

    OrderDetailMapper INSTANCE = Mappers.getMapper(OrderDetailMapper.class);

    ProductMapper productMapper = ProductMapper.INSTANCE;
    ProductDao productRepository = ProductDaoImpl.getProductRepository();

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "totalAmount", expression = "java(calculateTotalAmount(orderDetailNewDtoIn.getProductIds()))")
    OrderDetail orderDetailNewDtoInToOrderDetail(OrderDetailNewDtoIn orderDetailNewDtoIn);

    @Mapping(target = "products", expression = "java(productIdsToProductDtoOutList(orderDetail.getProductIds()))")
    OrderDetailDtoOut orderDetailToOrderDetailDtoOut(OrderDetail orderDetail);

    default List<ProductShortDtoOut> productIdsToProductDtoOutList(List<Long> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            return List.of();
        }
        return productMapper.productListToProductShortDtoOutList(productRepository.findByIds(productIds));
    }

    @Mapping(target = "totalAmount", expression = "java(calculateTotalAmount(orderDetailUpdateDtoIn.getProductIds()))")
    OrderDetail orderDetailUpdateDtoInToOrderDetail(OrderDetailUpdateDtoIn orderDetailUpdateDtoIn);

    default BigDecimal calculateTotalAmount(List<Long> ids) {

        List<Product> products = productRepository.findByIds(ids);
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (Product product : products) {
            totalAmount = totalAmount.add(product.getPrice());
        }
        return totalAmount;
    }

    List<OrderDetailDtoOut> orderDetailDtoOutListToOrderDetailDtoOutList(List<OrderDetail> orderDetails);
}
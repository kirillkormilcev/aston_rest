package ru.aston.rest_service.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;

/**
 * Product entity
 * Relation:
 * MtM: Product <-> ProductCategory
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@ToString
public class Product {
    Long id;
    String name;
    BigDecimal price;
    int quantity;
    boolean available;
    List<Long> productCategoryIds;

    /**
     * for test
     * */
    public Product(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Product product = (Product) o;
        return quantity == product.quantity && available == product.available && id.equals(product.id) && name.equals(product.name) && price.equals(product.price) && productCategoryIds.equals(product.productCategoryIds);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + price.hashCode();
        result = 31 * result + quantity;
        result = 31 * result + Boolean.hashCode(available);
        return result;
    }
}
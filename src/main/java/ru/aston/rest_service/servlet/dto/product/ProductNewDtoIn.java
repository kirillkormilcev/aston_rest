package ru.aston.rest_service.servlet.dto.product;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class ProductNewDtoIn {
    @NotNull
    @NotBlank
    String name;
    @Positive
    BigDecimal price;
    @Positive
    int quantity;
    @NotNull
    boolean available;
    @NotNull
    List<Long> productCategoryIds;

    /**
     * for test
     * */
    public ProductNewDtoIn (String name, List<Long> productCategoryIds) {
        this.name = name;
        this.productCategoryIds = productCategoryIds;
    }
}
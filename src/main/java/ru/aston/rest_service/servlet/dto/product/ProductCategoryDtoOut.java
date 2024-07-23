package ru.aston.rest_service.servlet.dto.product;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.aston.rest_service.model.CategoryType;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@ToString
public class ProductCategoryDtoOut {
    String name;
    CategoryType type;
}
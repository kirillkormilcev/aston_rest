package ru.aston.rest_service.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * Menu type
 * Relations:
 * OtM: -> CategoryType
 * Mtm: <-> Product
 */
@Getter
@Setter
@Builder(toBuilder = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@ToString
public class ProductCategory {
    Long id;
    String name;
    CategoryType type;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProductCategory that = (ProductCategory) o;
        return id.equals(that.id) && name.equals(that.name) && type == that.type;
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + type.hashCode();
        return result;
    }
}
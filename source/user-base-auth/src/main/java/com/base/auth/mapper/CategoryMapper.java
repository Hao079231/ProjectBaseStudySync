package com.base.auth.mapper;

import com.base.auth.form.category.CategoryForm;
import com.base.auth.model.Category;
import org.mapstruct.*;


@Mapper(componentModel = "spring")
public interface CategoryMapper {

    @Mapping(source = "name", target = "name")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "image", target = "image")
    @Mapping(source = "ordering", target = "ordering")
    @Mapping(source = "kind", target = "kind")
    @Named("fromCreateCategory")
    @BeanMapping(ignoreByDefault = true)
    Category fromCreateCategory(CategoryForm categoryForm);

    @Mapping(source = "name", target = "name")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "image", target = "image")
    @Mapping(source = "ordering", target = "ordering")
    @Named("mappingForUpdateCategory")
    @BeanMapping(ignoreByDefault = true)
    void mappingForUpdateServiceCategory(CategoryForm categoryForm, @MappingTarget Category category);
}

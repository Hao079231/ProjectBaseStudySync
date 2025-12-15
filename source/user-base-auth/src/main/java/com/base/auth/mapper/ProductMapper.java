package com.base.auth.mapper;

import com.base.auth.form.product.ProductForm;
import com.base.auth.model.Product;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProductMapper {
  @Mapping(source = "name", target = "name")
  @Mapping(source = "description", target = "description")
  @Mapping(source = "price", target = "price")
  @Mapping(source = "discount", target = "discount")
  @Mapping(source = "stock", target = "stock")
  @Mapping(source = "thumbnailUrl", target = "thumbnailUrl")
  @BeanMapping(ignoreByDefault = true)
  Product fromCreateProductFormToEntity(ProductForm productForm);

  @Mapping(source = "name", target = "name")
  @Mapping(source = "description", target = "description")
  @Mapping(source = "price", target = "price")
  @Mapping(source = "discount", target = "discount")
  @Mapping(source = "stock", target = "stock")
  @Mapping(source = "thumbnailUrl", target = "thumbnailUrl")
  @BeanMapping(ignoreByDefault = true)
  void fromUpdateProductFormToEntity(ProductForm productForm, @MappingTarget Product product);
}

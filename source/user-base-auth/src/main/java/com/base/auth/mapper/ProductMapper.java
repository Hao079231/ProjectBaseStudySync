package com.base.auth.mapper;

import com.base.auth.dto.product.ProductDto;
import com.base.auth.form.product.CreateProductForm;
import com.base.auth.form.product.UpdateProductForm;
import com.base.auth.model.Product;
import java.util.List;
import org.mapstruct.BeanMapping;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
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
  Product fromCreateProductFormToEntity(CreateProductForm createProductForm);

  @Mapping(source = "id", target = "id")
  @Mapping(source = "name", target = "name")
  @Mapping(source = "description", target = "description")
  @Mapping(source = "price", target = "price")
  @Mapping(source = "discount", target = "discount")
  @Mapping(source = "stock", target = "stock")
  @Mapping(source = "thumbnailUrl", target = "thumbnailUrl")
  @Mapping(source = "createdDate", target = "createdDate")
  @Mapping(source = "modifiedDate", target = "modifiedDate")
  @Mapping(source = "status", target = "status")
  @Mapping(source = "category", target = "category", qualifiedByName = "fromEntityToCategoryDto")
  @BeanMapping(ignoreByDefault = true)
  @Named("fromEntityToProductDto")
  ProductDto fromEntityToProductDto(Product product);

  @IterableMapping(elementTargetType = ProductDto.class, qualifiedByName = "fromEntityToProductDto")
  List<ProductDto> fromEntityToProductDtoList(List<Product> products);

  @Mapping(source = "name", target = "name")
  @Mapping(source = "description", target = "description")
  @Mapping(source = "price", target = "price")
  @Mapping(source = "discount", target = "discount")
  @Mapping(source = "stock", target = "stock")
  @Mapping(source = "thumbnailUrl", target = "thumbnailUrl")
  @BeanMapping(ignoreByDefault = true)
  void fromUpdateProductFormToEntity(UpdateProductForm updateProductForm, @MappingTarget Product product);
}

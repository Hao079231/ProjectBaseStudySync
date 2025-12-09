package com.base.auth.dto.product;

import com.base.auth.dto.ABasicAdminDto;
import com.base.auth.dto.category.CategoryDto;
import com.base.auth.model.Category;
import lombok.Data;

@Data
public class ProductDto extends ABasicAdminDto {
  private String name;
  private String description;
  private Double price;
  private Float discount;
  private Double finalPrice;
  private Integer stock;
  private String thumbnailUrl;
  private CategoryDto category;
}

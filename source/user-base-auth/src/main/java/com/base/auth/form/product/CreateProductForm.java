package com.base.auth.form.product;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Data;

@ApiModel
@Data
public class CreateProductForm {
  @NotEmpty(message = "name cannot be null")
  @ApiModelProperty(name = "name")
  private String name;
  @NotEmpty(message = "description cannot be null")
  @ApiModelProperty(name = "description")
  private String description;
  @NotNull(message = "price cannot be null")
  @ApiModelProperty(name = "price")
  private Double price;
  @NotNull(message = "discount cannot be null")
  @ApiModelProperty(name = "discount")
  private Float discount;
  private Integer stock;
  @ApiModelProperty(name = "thumbnailUrl")
  private String thumbnailUrl;
  @NotNull(message = "categoryId cannot be null")
  @ApiModelProperty(name = "categoryId")
  private Long categoryId;
}

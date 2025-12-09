package com.base.auth.controller;

import com.base.auth.dto.ApiMessageDto;
import com.base.auth.dto.ResponseListDto;
import com.base.auth.dto.product.ProductDto;
import com.base.auth.exception.NotFoundException;
import com.base.auth.form.product.CreateProductForm;
import com.base.auth.form.product.UpdateProductForm;
import com.base.auth.mapper.ProductMapper;
import com.base.auth.model.Category;
import com.base.auth.model.Product;
import com.base.auth.model.criteria.ProductCriteria;
import com.base.auth.repository.CategoryRepository;
import com.base.auth.repository.ProductRepository;
import java.util.List;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/product")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class ProductController extends ABasicController{
  @Autowired
  ProductRepository productRepository;

  @Autowired
  ProductMapper productMapper;

  @Autowired
  CategoryRepository categoryRepository;

  @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('PR_C')")
  public ApiMessageDto<String> create(@Valid @RequestBody CreateProductForm createProductForm, BindingResult bindingResult){
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    Category category = categoryRepository.findById(createProductForm.getCategoryId()).orElse(null);
    Product product = productMapper.fromCreateProductFormToEntity(createProductForm);
    Double finalPrice = createProductForm.getPrice() * (createProductForm.getDiscount() / 100);
    product.setFinalPrice(finalPrice);
    product.setCategory(category);
    productRepository.save(product);
    apiMessageDto.setMessage("Create product success");
    return apiMessageDto;
  }

  @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('PR_L')")
  public ApiMessageDto<ResponseListDto<List<ProductDto>>> list(ProductCriteria productCriteria, Pageable pageable){
    ApiMessageDto<ResponseListDto<List<ProductDto>>> apiMessageDto = new ApiMessageDto<>();
    ResponseListDto<List<ProductDto>> responseListDto = new ResponseListDto<>();
    Page<Product> products = productRepository.findAll(productCriteria.getSpecification(), pageable);
    responseListDto.setContent(productMapper.fromEntityToProductDtoList(products.getContent()));
    responseListDto.setTotalElements(products.getTotalElements());
    responseListDto.setTotalPages(products.getTotalPages());
    apiMessageDto.setData(responseListDto);
    apiMessageDto.setMessage("Get list product success");
    return apiMessageDto;
  }

  @PutMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('PR_U')")
  public ApiMessageDto<String> update(@Valid @RequestBody UpdateProductForm updateProductForm, BindingResult bindingResult){
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    Product product = productRepository.findById(updateProductForm.getId()).orElse(null);
    Category category = categoryRepository.findById(updateProductForm.getCategoryId()).orElse(null);
    productMapper.fromUpdateProductFormToEntity(updateProductForm, product);
    product.setCategory(category);
    productRepository.save(product);
    apiMessageDto.setMessage("Update product success");
    return apiMessageDto;
  }

  @DeleteMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('PR_D')")
  public ApiMessageDto<String> delete(@PathVariable("id") Long id){
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    Product product = productRepository.findById(id).orElse(null);
    productRepository.delete(product);
    apiMessageDto.setMessage("Delete product success");
    return apiMessageDto;
  }
}

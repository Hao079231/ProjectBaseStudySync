package com.base.auth.service.impl;

import com.base.auth.form.product.ProductForm;
import com.base.auth.mapper.ProductMapper;
import com.base.auth.model.Category;
import com.base.auth.model.Product;
import com.base.auth.repository.CategoryRepository;
import com.base.auth.repository.ProductRepository;
import com.base.auth.service.ISyncableService;
import com.base.auth.utils.ConvertUtils;
import com.base.auth.validation.SyncEntity;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@SyncEntity("PRODUCT")
@Slf4j
public class ProductSyncServiceImpl implements ISyncableService {

  @Autowired
  ProductRepository productRepository;

  @Autowired
  CategoryRepository categoryRepository;

  @Autowired
  ProductMapper productMapper;

  @Override
  public Boolean insert(Long id, Map<String, String> payload) {
    Long categoryId = extractCategoryId(payload);
    Category category = categoryRepository.findById(categoryId).orElse(null);
    if (category == null) {
      return false;
    }

    ProductForm form = mapToProductForm(payload);
    Product product = productMapper.fromCreateProductFormToEntity(form);
    Double finalPrice = form.getPrice() - (form.getPrice() * (form.getDiscount() / 100));
    product.setReusedId(id);
    product.setCategory(category);
    product.setFinalPrice(finalPrice);
    productRepository.save(product);
    return true;
  }

  @Override
  public Boolean update(Long id, Map<String, String> payload) {
    Product product = productRepository.findById(id).orElse(null);
    if (product == null) {
      return false;
    }

    Long categoryId = extractCategoryId(payload);
    Category category = categoryRepository.findById(categoryId).orElse(null);
    if (category == null) {
      return false;
    }

    ProductForm form = mapToProductForm(payload);
    productMapper.fromUpdateProductFormToEntity(form, product);
    Double finalPrice = form.getPrice() - (form.getPrice() * (form.getDiscount() / 100));
    product.setCategory(category);
    product.setFinalPrice(finalPrice);
    productRepository.save(product);
    return true;
  }

  @Override
  public Boolean delete(Long id) {
    if (!productRepository.existsById(id)) {
      return false;
    }
    productRepository.deleteById(id);
    return true;
  }

  private ProductForm mapToProductForm(Map<String, String> p) {
    ProductForm f = new ProductForm();
    f.setName(p.get("name"));
    f.setDescription(p.get("description"));
    f.setPrice(ConvertUtils.convertStringToDouble(p.get("price")));
    f.setDiscount(ConvertUtils.convertStringToFloat(p.get("discount")));
    f.setStock(ConvertUtils.convertStringToInteger(p.get("stock")));
    f.setThumbnailUrl(p.get("thumbnailUrl"));
    return f;
  }

  private Long extractCategoryId(Map<String, String> payload) {
    String value = payload.get("categoryId");
    return ConvertUtils.convertStringToLong(value);
  }
}

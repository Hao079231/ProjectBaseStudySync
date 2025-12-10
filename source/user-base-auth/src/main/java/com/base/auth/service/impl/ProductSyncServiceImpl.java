package com.base.auth.service.impl;

import com.base.auth.model.Product;
import com.base.auth.repository.CategoryRepository;
import com.base.auth.repository.ProductRepository;
import com.base.auth.service.ISyncableService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import javax.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ProductSyncServiceImpl implements ISyncableService {

  @Autowired
  ProductRepository productRepository;

  @Autowired
  CategoryRepository categoryRepository;

  @Autowired
  ObjectMapper objectMapper;

  @Override
  public Boolean insert(Long id, Map<String, Object> payload) {
    try{
      payload.remove("id");
      Product product = objectMapper.convertValue(payload, Product.class);
      product.setReusedId(id);
      productRepository.save(product);
      return true;
    }catch (Exception e){
      log.error("===> INSERT PRODUCT ERROR: {}", e.getMessage());
     return false;
    }
  }

  @Override
  public Boolean update(Long id, Map<String, Object> payload) {
    try {
      Product product = productRepository.findById(id).orElse(null);
      if (product == null){
        return false;
      }
      Long categoryId = extractCategoryId(payload);
      Boolean existCategory = categoryRepository.existsById(categoryId);
      if (!existCategory){
        return false;
      }
      objectMapper.updateValue(product, payload);
      productRepository.save(product);
      return true;
    } catch (Exception e) {
      log.error("===> UPDATE PRODUCT ERROR - ID: {}, Error: {}", id, e.getMessage());
      return false;
    }
  }

  @Override
  public Boolean delete(Long id) {
    try {
      if (!productRepository.existsById(id)) {
        return false;
      }
      productRepository.deleteById(id);
      return true;
    } catch (Exception e) {
      log.error("===> DELETE PRODUCT ERROR - ID: {}, Error: {}", id, e.getMessage());
      return false;
    }
  }

  private Long extractCategoryId(Map<String, Object> payload) {
    if (payload.containsKey("category")) {
      Object categoryObj = payload.get("category");
      if (categoryObj instanceof Map) {
        Map<String, Object> categoryMap = (Map<String, Object>) categoryObj;
        if (categoryMap.containsKey("id")) {
          Object idObj = categoryMap.get("id");
          if (idObj != null) {
            return Long.valueOf(idObj.toString());
          }
        }
      }
    }
    return null;
  }
}

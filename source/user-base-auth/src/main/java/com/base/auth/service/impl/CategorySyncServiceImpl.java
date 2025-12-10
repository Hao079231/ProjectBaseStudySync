package com.base.auth.service.impl;

import com.base.auth.model.Category;
import com.base.auth.repository.CategoryRepository;
import com.base.auth.service.ISyncableService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import javax.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CategorySyncServiceImpl implements ISyncableService {

  @Autowired
  CategoryRepository categoryRepository;

  @Autowired
  ObjectMapper objectMapper;

  @Override
  @Transactional
  public Boolean insert(Long id, Map<String, Object> payload) {
    try{
      payload.remove("id");
      Category category = objectMapper.convertValue(payload, Category.class);
      category.setReusedId(id);
      categoryRepository.save(category);
      return true;
    }catch (Exception e){
        log.error("===> INSERT CATEGORY ERROR: {}", e.getMessage());
     return false;
    }
  }

  @Override
  public Boolean update(Long id, Map<String, Object> payload) {
    try {
      Category category = categoryRepository.findById(id).orElse(null);
      if (category == null){
        return false;
      }
      objectMapper.updateValue(category, payload);
      category.setReusedId(id);
      categoryRepository.save(category);
      return true;
    } catch (Exception e) {
      log.error("===> UPDATE CATEGORY ERROR - ID: {}, Error: {}", id, e.getMessage());
      return false;
    }
  }

  @Override
  public Boolean delete(Long id) {
    try {
      if (!categoryRepository.existsById(id)) {
        return false;
      }
      categoryRepository.deleteById(id);
      return true;
    } catch (Exception e) {
      log.error("===> DELETE CATEGORY ERROR - ID: {}, Error: {}", id, e.getMessage());
      return false;
    }
  }
}

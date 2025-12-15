package com.base.auth.service.impl;

import com.base.auth.form.category.CategoryForm;
import com.base.auth.mapper.CategoryMapper;
import com.base.auth.model.Category;
import com.base.auth.repository.CategoryRepository;
import com.base.auth.service.ISyncableService;
import com.base.auth.utils.ConvertUtils;
import com.base.auth.validation.SyncEntity;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@SyncEntity("CATEGORY")
@Slf4j
public class CategorySyncServiceImpl implements ISyncableService {

  @Autowired
  CategoryRepository categoryRepository;

  @Autowired
  CategoryMapper categoryMapper;

  @Override
  public Boolean insert(Long id, Map<String, String> payload) {
    CategoryForm form = mapToCategoryForm(payload);
    Category category = categoryMapper.fromCreateCategory(form);
    category.setReusedId(id);
    categoryRepository.save(category);
    return true;
  }

  @Override
  public Boolean update(Long id, Map<String, String> payload) {
    Category category = categoryRepository.findById(id).orElse(null);
    if (category == null) {
      return false;
    }

    CategoryForm form = mapToCategoryForm(payload);
    categoryMapper.mappingForUpdateServiceCategory(form, category);
    categoryRepository.save(category);
    return true;
  }

  @Override
  public Boolean delete(Long id) {
    if (!categoryRepository.existsById(id)) {
      return false;
    }
    categoryRepository.deleteById(id);
    return true;
  }

  private CategoryForm mapToCategoryForm(Map<String, String> p) {
    CategoryForm f = new CategoryForm();
    f.setName(p.get("name"));
    f.setDescription(p.get("description"));
    f.setImage(p.get("image"));
    f.setOrdering(ConvertUtils.convertStringToInteger(p.get("ordering")));
    f.setKind(ConvertUtils.convertStringToInteger(p.get("kind")));
    return f;
  }
}


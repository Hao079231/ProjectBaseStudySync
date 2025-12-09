package com.base.auth.model.criteria;

import com.base.auth.model.Product;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

@Data
public class ProductCriteria {
  private String name;
  private Double minPrice;
  private Double maxPrice;

  public Specification<Product> getSpecification() {
    return new Specification<Product>() {
      private static final long serialVersionUID = 1L;

      @Override
      public Predicate toPredicate(Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();
        if(!StringUtils.isEmpty(getName())){
          predicates.add(cb.like(cb.lower(root.get("name")), "%" + getName().toLowerCase() + "%"));
        }

        if (getMinPrice() != null){
          predicates.add(cb.greaterThanOrEqualTo(root.get("finalPrice"), getMinPrice()));
        }

        if (getMaxPrice() != null){
          predicates.add(cb.lessThanOrEqualTo(root.get("finalPrice"), getMaxPrice()));
        }
        return cb.and(predicates.toArray(new Predicate[predicates.size()]));
      }
    };
  }
}

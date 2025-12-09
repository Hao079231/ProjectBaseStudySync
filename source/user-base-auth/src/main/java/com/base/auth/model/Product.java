package com.base.auth.model;

import com.base.auth.component.EntityListener;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "db_user_base_product")
@EntityListeners({AuditingEntityListener.class, EntityListener.class})
@Getter
@Setter
public class Product extends Auditable<String>{
  @Id
  @GenericGenerator(name = "idGenerator", strategy = "com.base.auth.service.id.IdGenerator")
  @GeneratedValue(generator = "idGenerator")
  private Long id;
  @Column(name = "name", unique = true, nullable = false)
  private String name;
  @Column(name = "description" ,  columnDefinition = "TEXT")
  private String description;
  private Double price;
  private Float discount;
  private Double finalPrice;
  private Integer stock;
  private String thumbnailUrl;
  @ManyToOne
  @JoinColumn(name = "category_id", nullable = false)
  private Category category;
}

package com.base.auth.model;

import com.base.auth.component.EntityListener;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "db_sync_log_history")
@EntityListeners({AuditingEntityListener.class, EntityListener.class})
@Getter
@Setter
public class SyncLogHistory extends Auditable<String>{
  @Id
  @GenericGenerator(name = "idGenerator", strategy = "com.base.auth.service.id.IdGenerator")
  @GeneratedValue(generator = "idGenerator")
  private Long id;
  private String entity;
  private String type;
  @Column(columnDefinition = "TEXT")
  private String payload;
}

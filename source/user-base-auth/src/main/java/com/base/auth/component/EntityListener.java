package com.base.auth.component;

import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Slf4j
public class EntityListener {
  @PostPersist
  public void afterInsert(Object entity) {
    log.info("===> DB success insert entity={} by={}", entity.getClass().getSimpleName(), currentUser());
  }

  @PostUpdate
  public void afterUpdate(Object entity) {
    log.info("===> DB success update entity={} by={}", entity.getClass().getSimpleName(), currentUser());
  }

  @PostRemove
  public void afterDelete(Object entity) {
    log.info("===> DB success delete entity={} by={}", entity.getClass().getSimpleName(), currentUser());
  }

  private String currentUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    return (authentication != null && authentication.isAuthenticated())
        ? authentication.getName()
        : "anonymousUser";
  }
}

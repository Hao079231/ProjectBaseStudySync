package com.base.auth.service;

import java.util.Map;

public interface ISyncableService {
  Boolean insert(Long id, Map<String, Object> payload);
  Boolean update(Long id, Map<String, Object> payload);
  Boolean delete(Long id);
}

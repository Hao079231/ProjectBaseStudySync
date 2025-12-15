package com.base.auth.service;

import java.util.Map;

public interface ISyncableService {
  Boolean insert(Long id, Map<String, String> payload);
  Boolean update(Long id, Map<String, String> payload);
  Boolean delete(Long id);
}

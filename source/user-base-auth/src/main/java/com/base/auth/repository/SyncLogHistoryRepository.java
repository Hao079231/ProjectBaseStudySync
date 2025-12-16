package com.base.auth.repository;

import com.base.auth.model.SyncLogHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SyncLogHistoryRepository extends JpaRepository<SyncLogHistory, Long> {

  Boolean existsByIdAndStatus(Long syncLogId, Integer statusActive);
}

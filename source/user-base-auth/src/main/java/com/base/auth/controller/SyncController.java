package com.base.auth.controller;

import com.base.auth.constant.UserBaseConstant;
import com.base.auth.dto.ApiMessageDto;
import com.base.auth.form.sync.DataSyncRequestForm;
import com.base.auth.model.SyncLogHistory;
import com.base.auth.repository.SyncLogHistoryRepository;
import com.base.auth.service.SyncService;
import java.util.Objects;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/sync")
@Slf4j
public class SyncController {
  @Autowired
  SyncService syncService;

  @Autowired
  SyncLogHistoryRepository syncLogHistoryRepository;

  @PostMapping(value = "/process")
  public ApiMessageDto<Boolean> handleSync(@Valid @RequestBody DataSyncRequestForm dataSyncRequestForm, BindingResult bindingResult){
    ApiMessageDto<Boolean> apiMessageDto = new ApiMessageDto<>();
    SyncLogHistory syncLogHistory = syncLogHistoryRepository.findById(dataSyncRequestForm.getSyncLogId()).orElse(null);

    if (syncLogHistory == null){
      syncLogHistory = new SyncLogHistory();
      syncLogHistory.setReusedId(dataSyncRequestForm.getSyncLogId());
      syncLogHistory.setEntity(dataSyncRequestForm.getEntity());
      syncLogHistory.setType(dataSyncRequestForm.getType());
      syncLogHistory.setPayload(dataSyncRequestForm.getPayload());
    } else if (Objects.equals(syncLogHistory.getStatus(), UserBaseConstant.SYNC_STATUS_SUCCESS)) {
      apiMessageDto.setMessage("Data sync is done");
      return apiMessageDto;
    }

    Boolean result = syncService.processSync(dataSyncRequestForm);
    if (!result){
      syncLogHistory.setStatus(UserBaseConstant.SYNC_STATUS_PROGRESS);
      apiMessageDto.setResult(false);
      apiMessageDto.setMessage("Sync data failed");
    } else {
      syncLogHistory.setStatus(UserBaseConstant.SYNC_STATUS_SUCCESS);
      apiMessageDto.setMessage("Sync data success");
    }
    syncLogHistoryRepository.save(syncLogHistory);
    return apiMessageDto;
  }
}

package com.base.auth.controller;

import com.base.auth.dto.ApiMessageDto;
import com.base.auth.form.sync.DataSyncRequestForm;
import com.base.auth.service.SyncService;
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

  @PostMapping(value = "/process")
  public ApiMessageDto<Boolean> handleSync(@Valid @RequestBody DataSyncRequestForm dataSyncRequestForm, BindingResult bindingResult){
    ApiMessageDto<Boolean> apiMessageDto = new ApiMessageDto<>();
    Boolean result = syncService.processSync(dataSyncRequestForm);
    if (!result){
      apiMessageDto.setResult(false);
      apiMessageDto.setMessage("Sync data failed");
      return apiMessageDto;
    }
    apiMessageDto.setMessage("Sync data success");
    return apiMessageDto;
  }
}

package com.base.auth.service;

import com.base.auth.constant.UserBaseConstant;
import com.base.auth.form.sync.DataSyncRequestForm;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SyncService {
  @Autowired
  ApplicationContext applicationContext;

  @Autowired
  ObjectMapper objectMapper;

  public Boolean processSync(DataSyncRequestForm dataSyncRequestForm){
    try{
      String serviceName = UserBaseConstant.ENTITY_SERVICE_MAP.get(dataSyncRequestForm.getEntity());
      if (StringUtils.isEmpty(serviceName)){
        log.error("===> SYNC UNSUPPORTED - Entity: {}", dataSyncRequestForm.getEntity());
        return false;
      }

      ISyncableService service = (ISyncableService) applicationContext.getBean(serviceName);
      Map<String, Object> payloadMap = parsePayload(dataSyncRequestForm.getPayload());
      String type = dataSyncRequestForm.getType().toUpperCase();

      switch (type) {
        case UserBaseConstant.SYNC_TYPE_INSERT:
          return handleInsert(service, dataSyncRequestForm.getEntity(), payloadMap);

        case UserBaseConstant.SYNC_TYPE_UPDATE:
          return handleUpdate(service, dataSyncRequestForm.getEntity(), payloadMap);

        case UserBaseConstant.SYNC_TYPE_DELETE:
          return handleDelete(service, dataSyncRequestForm.getEntity(), payloadMap);

        default:
          log.error("===> SYNC-INVALID-TYPE - Type: {}", dataSyncRequestForm.getType());
          return false;
      }

    } catch (Exception e) {
      log.error("===> SYNC-EXCEPTION - Entity: {}, Error: {}", dataSyncRequestForm.getEntity(), e.getMessage());
      return false;
    }
  }

  private Boolean handleInsert(ISyncableService service, String entityName,
      Map<String, Object> payload) {
    log.info("===> SYNC INSERT - Entity: {}", entityName);
    Object idObj = payload.get("id");
    Long id = Long.valueOf(idObj.toString());
    return service.insert(id, payload);
  }

  private Boolean handleUpdate(ISyncableService service, String entityName,
      Map<String, Object> payload) {
    log.info("===> SYNC-UPDATE - Entity: {}", entityName);

    Object idObj = payload.get("id");
    Long id = Long.valueOf(idObj.toString());
    return service.update(id, payload);
  }

  Boolean handleDelete(ISyncableService service, String entityName,
      Map<String, Object> payload) {
    log.info("===> SYNC DELETE - Entity: {}", entityName);

    Object idObj = payload.get("id");
    Long id = Long.valueOf(idObj.toString());
    return service.delete(id);
  }


  private Map<String, Object> parsePayload(String payload) {
    try {
      if (payload == null || payload.isEmpty()) {
        log.error("===> SYNC PARSE ERROR: Payload is emty");
      }
      return objectMapper.readValue(payload, new TypeReference<Map<String, Object>>() {});

    } catch (Exception e) {
      log.error("===> SYNC PARSE ERROR - Failed to parse payload: {}", e.getMessage());
      return null;
    }
  }
}

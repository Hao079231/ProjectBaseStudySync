package com.base.auth.service;

import com.base.auth.constant.UserBaseConstant;
import com.base.auth.form.sync.DataSyncRequestForm;
import com.base.auth.model.SyncLogHistory;
import com.base.auth.repository.SyncLogHistoryRepository;
import com.base.auth.utils.ConvertUtils;
import com.base.auth.utils.JsonPayloadParserUtils;
import com.base.auth.validation.SyncEntity;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SyncService {
  @Autowired
  ApplicationContext applicationContext;
  private Map<String, ISyncableService> entityServiceMap = new HashMap<>();

  @Autowired
  SyncLogHistoryRepository syncLogHistoryRepository;

  @PostConstruct
  public void init(){
    Map<String, ISyncableService> services = applicationContext.getBeansOfType(ISyncableService.class);

    for (ISyncableService service : services.values()){
      SyncEntity anotation = service.getClass().getAnnotation(SyncEntity.class);
      if (anotation != null){
        String entityName = anotation.value().toUpperCase();
        entityServiceMap.put(entityName, service);
      }
    }
  }

  public Boolean processSync(DataSyncRequestForm form) {
    try {
      ISyncableService service = entityServiceMap.get(form.getEntity().toUpperCase());

      if (service == null) {
        log.error("SYNC ENTITY NOT FOUND: {}", form.getEntity());
        return false;
      }

      String type = form.getType().toUpperCase();
      Map<String, String> payload = JsonPayloadParserUtils.parse(form.getPayload());

      Long id = ConvertUtils.convertStringToLong(payload.get("id"));
      Boolean result = false;
      SyncLogHistory syncLogHistory = new SyncLogHistory();
      syncLogHistory.setReusedId(form.getSyncLogId());
      syncLogHistory.setEntity(form.getEntity());
      syncLogHistory.setType(form.getType());
      syncLogHistory.setPayload(form.getPayload());
      syncLogHistory.setStatus(UserBaseConstant.SYNC_STATUS_PROGRESS);

      switch (type) {
        case UserBaseConstant.SYNC_TYPE_INSERT:
          result = service.insert(id, payload);
          break;

        case UserBaseConstant.SYNC_TYPE_UPDATE:
          result = service.update(id, payload);
          break;

        case UserBaseConstant.SYNC_TYPE_DELETE:
          result = service.delete(id);
          break;

        default:
          log.error("INVALID SYNC TYPE: {}", type);
          return false;
      }

      if (result){
        syncLogHistory.setStatus(UserBaseConstant.SYNC_STATUS_SUCCESS);
      }
      syncLogHistoryRepository.save(syncLogHistory);
      return result;
    } catch (Exception e) {
      log.error("SYNC ERROR", e);
      return false;
    }
  }
}

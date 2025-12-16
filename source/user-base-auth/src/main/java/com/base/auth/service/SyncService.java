package com.base.auth.service;

import com.base.auth.constant.UserBaseConstant;
import com.base.auth.form.sync.DataSyncRequestForm;
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

      switch (type) {
        case UserBaseConstant.SYNC_TYPE_INSERT:
          return service.insert(id, payload);

        case UserBaseConstant.SYNC_TYPE_UPDATE:
          return service.update(id, payload);

        case UserBaseConstant.SYNC_TYPE_DELETE:
          return service.delete(id);

        default:
          log.error("INVALID SYNC TYPE: {}", type);
          return false;
      }
    } catch (Exception e) {
      log.error("SYNC ERROR", e);
      return false;
    }
  }
}

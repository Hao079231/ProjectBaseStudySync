package com.base.auth.form.sync;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DataSyncRequestForm {
  @NotNull(message = "syncLogId cannot be null")
  private Long syncLogId;
  @NotEmpty(message = "entity cannot be null")
  private String entity;
  @NotEmpty(message = "type cannot be null")
  private String type;
  @NotEmpty(message = "payload cannot be null")
  private String payload;
}

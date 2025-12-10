package com.base.auth.form.sync;

import javax.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class DataSyncRequestForm {
  @NotEmpty(message = "entity cannot be null")
  private String entity;
  @NotEmpty(message = "type cannot be null")
  private String type;
  @NotEmpty(message = "payload cannot be null")
  private String payload;
}

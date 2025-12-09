package com.base.auth.form.account;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@ApiModel
@Data
public class AccountProfileDto {
  private String username;
  private String email;
  private String phone;
  private String fullName;
}

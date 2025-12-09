package com.base.auth.form.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotEmpty;
import lombok.Data;

@Data
@ApiModel
public class VerifyOtpLoginForm {
  @NotEmpty(message = "phone cannot be null")
  @ApiModelProperty(name = "phone")
  private String phone;
  @NotEmpty(message = "otp cannot be null")
  @ApiModelProperty(name = "otp")
  private String otp;
}

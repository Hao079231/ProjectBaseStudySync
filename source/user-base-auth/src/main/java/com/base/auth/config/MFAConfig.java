package com.base.auth.config;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class MFAConfig {
  @Bean
  public GoogleAuthenticator googleAuthenticator(){
    GoogleAuthenticatorConfig config = new GoogleAuthenticatorConfig
        .GoogleAuthenticatorConfigBuilder()
        .setTimeStepSizeInMillis(30000)
        .setWindowSize(1)
        .build();
    return new GoogleAuthenticator(config);
  }
}

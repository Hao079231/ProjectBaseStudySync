package com.base.auth.service;

import com.base.auth.repository.UserRepository;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
@Slf4j
public class MFAService {

  @Value("${mfa.issuer}")
  private String issuer;

  @Value("${mfa.qr.url.template}")
  private String qrUrlTemplate;

  @Autowired
  private GoogleAuthenticator googleAuthenticator;

  @Autowired
  private UserRepository userRepository;
  public String generateSecretKeyForUser() {
    GoogleAuthenticatorKey key = googleAuthenticator.createCredentials();
    return key.getKey();
  }

  public String generateQrCodeUrl(String secretKey, String accountIdentifier) {
    String encodedIssuer = URLEncoder.encode(issuer, StandardCharsets.UTF_8);
    String encodedAccount = URLEncoder.encode(accountIdentifier, StandardCharsets.UTF_8);

    return qrUrlTemplate
        .replace("{ISSUER}", encodedIssuer)
        .replace("{ACCOUNT}", encodedAccount)
        .replace("{SECRET}", secretKey);
  }

  public boolean verifyOtp(String secretKey, String otpCode) {
    try {
      int code = Integer.parseInt(otpCode);
      return googleAuthenticator.authorize(secretKey, code);
    } catch (Exception e) {
      log.error("Failed to verify OTP", e);
      return false;
    }
  }
}

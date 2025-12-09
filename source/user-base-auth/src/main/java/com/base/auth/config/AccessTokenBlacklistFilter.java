package com.base.auth.config;

import com.base.auth.jwt.UserBaseJwt;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class AccessTokenBlacklistFilter extends OncePerRequestFilter {
  @Value("${spring.global.version}")
  private int globalVersion;

  @Override
  protected void doFilterInternal(HttpServletRequest request,
      HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth instanceof OAuth2Authentication) {
      OAuth2AuthenticationDetails details = (OAuth2AuthenticationDetails) auth.getDetails();
      if (details != null) {
        Map<String, Object> map = (Map<String, Object>) details.getDecodedDetails();
        String encodedData = (String) map.get("additional_info");
        if (encodedData != null && !encodedData.isEmpty()) {
          UserBaseJwt userBaseJwt = UserBaseJwt.decode(encodedData);
          if (userBaseJwt != null) {
            Integer tokenGlobalVersion = (Integer) map.get("global_version");
            if (!Objects.equals(tokenGlobalVersion, globalVersion)) {
              response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token revoked");
              return;
            }
          }
        }
      }
    }
    filterChain.doFilter(request, response);
  }
}
package com.base.auth.utils;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

public final class JsonPayloadParserUtils {

  private JsonPayloadParserUtils() {}

  public static Map<String, String> parse(String json) {
    Map<String, String> map = new HashMap<>();

    if (StringUtils.isEmpty(json)) {
      return map;
    }

    String body = json.trim();
    if (body.startsWith("{")) body = body.substring(1);
    if (body.endsWith("}")) body = body.substring(0, body.length() - 1);

    String[] pairs = body.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

    for (String pair : pairs) {
      String[] kv = pair.split(":", 2);
      if (kv.length != 2) continue;

      String key = clean(kv[0]);
      String value = clean(kv[1]);

      map.put(key, value);
    }

    return map;
  }

  private static String clean(String value) {
    value = value.trim();
    if ("null".equals(value)) return null;

    if (value.startsWith("\"") && value.endsWith("\"")) {
      value = value.substring(1, value.length() - 1);
    }

    return value
        .replace("\\\"", "\"")
        .replace("\\n", "\n")
        .replace("\\r", "\r")
        .replace("\\t", "\t")
        .replace("\\\\", "\\");
  }
}

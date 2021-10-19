package org.mule.tooling.restsdk.utils;

import java.util.HashMap;
import java.util.Map;

public class MapUtils {

  public static Map<String, String> map(String... entries) {
    HashMap<String, String> result = new HashMap<>();
    for (int i = 0; i < entries.length; i = i + 2) {
      String key = entries[i];
      String value = entries[i + 1];
      result.put(key, value);
    }
    return result;
  }
}

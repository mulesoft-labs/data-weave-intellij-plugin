package org.mule.tooling.restsdk.utils;

import java.util.Set;

import static com.google.common.base.CaseFormat.*;
import static com.intellij.openapi.util.text.StringUtil.pluralize;
import static org.apache.commons.lang3.StringUtils.*;

public class JavaUtils {

  private final static int DEFAULT_MAX_TEXT_LENGTH = 300;

  private JavaUtils() {}

  private static final Set<String> reservedJavaWords = Set.of(
                                                                      "abstract", "assert", "boolean", "break", "byte", "case",
                                                                      "catch", "char", "class", "const", "continue",
                                                                      "default", "do", "double", "else", "extends",
                                                                      "false", "final", "finally", "float", "for",
                                                                      "goto", "if", "implements", "import", "instanceof",
                                                                      "int", "interface", "long", "native", "new",
                                                                      "null", "package", "private", "protected", "public",
                                                                      "return", "short", "static", "strictfp", "super",
                                                                      "switch", "synchronized", "this", "throw", "throws",
                                                                      "transient", "true", "try", "void", "volatile",
                                                                      "while");


  public static String getJavaLowerCamelNameFromXml(String name) {
    return removeJavaNameUnwantedCharacters(LOWER_HYPHEN.to(LOWER_CAMEL, name));
  }

  public static String getJavaUpperCamelNameFromXml(String name) {
    return removeJavaNameUnwantedCharacters(LOWER_HYPHEN.to(UPPER_CAMEL, name));
  }

  public static String getJavaConstantNameFromXml(String name) {
    return removeJavaNameUnwantedCharacters(LOWER_HYPHEN.to(UPPER_UNDERSCORE, name));
  }

  public static String removeJavaNameUnwantedCharacters(String name) {
    return removeJavaNameUnwantedCharacters(name, "j");
  }

  public static String removeJavaNameUnwantedCharacters(String name, String sanitizeSufix) {
    if (name == null) {
      return null;
    }

    String javaName = name.replaceAll("[^a-zA-Z0-9_]", EMPTY);

    if (javaName.matches("^[0-9_].*")) {
      javaName = sanitizeSufix + javaName;
    }

    if (javaName.isBlank()) {
      return "empty";
    }

    return javaName;
  }

  public static String removeJavaPackageUnwantedCharacters(String name) {
    if (name == null) {
      return null;
    }

    String[] split = name.split("[.]");

    StringBuilder packageString = new StringBuilder();
    for (int i = 0; i < split.length; i++) {
      String s = removeJavaNameUnwantedCharacters(split[i]);
      if (!s.isBlank()) {
        packageString.append(s.toLowerCase());
        if (i < split.length - 1) {
          packageString.append(".");
        }
      }
    }

    return packageString.toString();
  }

  public static boolean isReservedJavaWord(String word) {
    return reservedJavaWords.contains(word);
  }

  public static String abbreviateText(String text, int maxLength) {
    if (text.isBlank() || text.length() <= maxLength) {
      return text;
    }

    String firstText = substring(text, 0, maxLength);
    int lastIndexOfWithDot = firstText.lastIndexOf(".");

    return lastIndexOfWithDot == -1
        ? abbreviate(text, maxLength)
        : substring(firstText, 0, lastIndexOfWithDot + 1);
  }

  public static String abbreviateText(String text) {
    return abbreviateText(text, DEFAULT_MAX_TEXT_LENGTH);
  }

  public static String getParameterJavaName(String internalName, boolean isArray) {
    String javaName = getJavaLowerCamelNameFromXml(internalName);

    if (isArray) {
      javaName = pluralize(javaName);
    }

    if (isReservedJavaWord(javaName)) {
      return "j" + javaName;
    } else {
      return javaName;
    }
  }

}
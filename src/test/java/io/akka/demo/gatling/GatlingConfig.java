package io.akka.demo.gatling;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.time.Duration;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GatlingConfig {
  private final Properties properties = new Properties();

  static GatlingConfig load(String configFilePath) {
    return new GatlingConfig(configFilePath);
  }

  static GatlingConfig load(File file) {
    return new GatlingConfig(file);
  }

  private GatlingConfig() {
    this("gatling.conf");
  }

  private GatlingConfig(String configFilePath) {
    try (InputStream fis = getClass().getClassLoader().getResourceAsStream(configFilePath)) {
      properties.load(fis);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private GatlingConfig(File file) {
    try (InputStream fis = new FileInputStream(file)) {
      properties.load(fis);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private String getProperty(String key) {
    if (key == null || key.isEmpty()) {
      throw new IllegalArgumentException("Key cannot be null or empty");
    }
    String value = properties.getProperty(key, null);
    if (value == null) {
      throw new IllegalArgumentException("Key not found: " + key);
    }
    return value;
  }

  String getString(String key) {
    return getProperty(key);
  }

  int getInt(String key) {
    return Integer.parseInt(getProperty(key));
  }

  long getLong(String key) {
    return Long.parseLong(getProperty(key));
  }

  Duration getDuration(String key) {
    return parseDuration(getProperty(key));
  }

  static Duration parseDuration(String durationStr) {
    if (durationStr == null) {
      throw new IllegalArgumentException("Duration string cannot be null");
    }

    try {
      Pattern pattern = Pattern.compile("(\\d+)(ms|s|m|h|d)");
      Matcher matcher = pattern.matcher(durationStr);
      if (matcher.matches()) {
        long value = Long.parseLong(matcher.group(1));
        String unit = matcher.group(2);
        switch (unit) {
          case "ms":
            return Duration.ofMillis(value);
          case "s":
            return Duration.ofSeconds(value);
          case "m":
            return Duration.ofMinutes(value);
          case "h":
            return Duration.ofHours(value);
          case "d":
            return Duration.ofDays(value);
          default:
            throw new IllegalArgumentException("Unknown duration unit: " + unit);
        }
      } else {
        throw new IllegalArgumentException("Invalid duration format: " + durationStr);
      }
    } catch (NullPointerException e) {
      throw new IllegalArgumentException("Invalid duration format: " + durationStr, e);
    }
  }
}

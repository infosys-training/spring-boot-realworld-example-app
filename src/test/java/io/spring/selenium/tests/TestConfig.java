package io.spring.selenium.tests;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Test configuration class that loads test credentials from environment variables or properties
 * file. Environment variables take precedence over properties file values.
 *
 * <p>Required environment variables for test execution: - TEST_USER_A_EMAIL, TEST_USER_A_PASSWORD,
 * TEST_USER_A_USERNAME - TEST_USER_B_EMAIL, TEST_USER_B_PASSWORD, TEST_USER_B_USERNAME -
 * TEST_USER_C_EMAIL, TEST_USER_C_PASSWORD, TEST_USER_C_USERNAME
 *
 * <p>Or configure via src/test/resources/selenium/test-data.properties
 */
public class TestConfig {

  private static final String CONFIG_PATH = "src/test/resources/selenium/config.properties";
  private static final String TEST_DATA_PATH = "src/test/resources/selenium/test-data.properties";
  private static Properties config;
  private static Properties testData;

  static {
    loadConfig();
    loadTestData();
  }

  private static void loadConfig() {
    config = new Properties();
    try {
      config.load(new FileInputStream(CONFIG_PATH));
    } catch (IOException e) {
      System.out.println("Config file not found, using defaults");
    }
  }

  private static void loadTestData() {
    testData = new Properties();
    try {
      testData.load(new FileInputStream(TEST_DATA_PATH));
    } catch (IOException e) {
      System.out.println("Test data file not found, will use environment variables");
    }
  }

  private static String getEnvOrProperty(String envKey, String propKey) {
    String envValue = System.getenv(envKey);
    if (envValue != null && !envValue.isEmpty()) {
      return envValue;
    }
    return testData.getProperty(propKey, "");
  }

  public static String getBaseUrl() {
    String envValue = System.getenv("TEST_BASE_URL");
    if (envValue != null && !envValue.isEmpty()) {
      return envValue;
    }
    return config.getProperty("base.url", "http://localhost:3000");
  }

  public static String getApiUrl() {
    String envValue = System.getenv("TEST_API_URL");
    if (envValue != null && !envValue.isEmpty()) {
      return envValue;
    }
    return config.getProperty("api.url", "http://localhost:8080");
  }

  public static String getUserAEmail() {
    return getEnvOrProperty("TEST_USER_A_EMAIL", "user.a.email");
  }

  public static String getUserAPassword() {
    return getEnvOrProperty("TEST_USER_A_PASSWORD", "user.a.password");
  }

  public static String getUserAUsername() {
    return getEnvOrProperty("TEST_USER_A_USERNAME", "user.a.username");
  }

  public static String getUserBEmail() {
    return getEnvOrProperty("TEST_USER_B_EMAIL", "user.b.email");
  }

  public static String getUserBPassword() {
    return getEnvOrProperty("TEST_USER_B_PASSWORD", "user.b.password");
  }

  public static String getUserBUsername() {
    return getEnvOrProperty("TEST_USER_B_USERNAME", "user.b.username");
  }

  public static String getUserCEmail() {
    return getEnvOrProperty("TEST_USER_C_EMAIL", "user.c.email");
  }

  public static String getUserCPassword() {
    return getEnvOrProperty("TEST_USER_C_PASSWORD", "user.c.password");
  }

  public static String getUserCUsername() {
    return getEnvOrProperty("TEST_USER_C_USERNAME", "user.c.username");
  }
}

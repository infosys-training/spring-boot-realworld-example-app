package io.spring.selenium.tests;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import io.github.bonigarcia.wdm.WebDriverManager;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.ITestResult;
import org.testng.annotations.*;

/** Base test class providing WebDriver setup, teardown, and reporting. */
public abstract class BaseTest {

  protected WebDriver driver;
  protected static ExtentReports extent;
  protected ExtentTest test;
  protected static Properties config;

  private static final String CONFIG_PATH = "src/test/resources/selenium/config.properties";
  private static final String REPORT_PATH = "build/reports/selenium/ExtentReport.html";
  private static final String SCREENSHOT_PATH = "build/reports/selenium/screenshots/";

  @BeforeSuite
  public void setupSuite() {
    loadConfig();
    setupExtentReports();
    createDirectories();
  }

  @BeforeMethod
  public void setupTest() {
    initializeDriver();
    driver.manage().window().maximize();

    // Apply timeout settings from config
    int implicitWait = Integer.parseInt(config.getProperty("implicit.wait", "5"));
    int pageLoadTimeout = Integer.parseInt(config.getProperty("page.load.timeout", "15"));

    driver.manage().timeouts().implicitlyWait(implicitWait, TimeUnit.SECONDS);
    driver.manage().timeouts().pageLoadTimeout(pageLoadTimeout, TimeUnit.SECONDS);

    // Don't navigate to base URL in setup - let individual tests handle navigation
  }

  @AfterMethod
  public void teardownTest(ITestResult result) {
    if (result.getStatus() == ITestResult.FAILURE) {
      captureScreenshot(result.getName());
      test.fail("Test failed: " + result.getThrowable().getMessage());
    } else if (result.getStatus() == ITestResult.SUCCESS) {
      test.pass("Test passed");
    } else {
      test.skip("Test skipped");
    }

    // Don't quit the driver when using Devin's browser - it would close Devin's browser instance
    boolean useDevinBrowser =
        Boolean.parseBoolean(config.getProperty("devin.browser.enabled", "false"));
    if (driver != null && !useDevinBrowser) {
      driver.quit();
    }
  }

  @AfterSuite
  public void teardownSuite() {
    if (extent != null) {
      extent.flush();
    }
  }

  private void loadConfig() {
    config = new Properties();
    try {
      File configFile = new File(CONFIG_PATH);
      if (configFile.exists()) {
        config.load(new FileInputStream(configFile));
      }
    } catch (IOException e) {
      System.out.println("Config file not found, using defaults");
    }
  }

  private void setupExtentReports() {
    ExtentSparkReporter sparkReporter = new ExtentSparkReporter(REPORT_PATH);
    sparkReporter.config().setTheme(Theme.STANDARD);
    sparkReporter.config().setDocumentTitle("Selenium Test Report");
    sparkReporter.config().setReportName("Test Execution Report");

    extent = new ExtentReports();
    extent.attachReporter(sparkReporter);
    extent.setSystemInfo("OS", System.getProperty("os.name"));
    extent.setSystemInfo("Java Version", System.getProperty("java.version"));
  }

  private void createDirectories() {
    try {
      Files.createDirectories(Paths.get(SCREENSHOT_PATH));
      Files.createDirectories(Paths.get("build/reports/selenium/archive"));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void initializeDriver() {
    String browser = config.getProperty("browser", "chrome").toLowerCase();
    boolean useDevinBrowser =
        Boolean.parseBoolean(config.getProperty("devin.browser.enabled", "false"));

    if (useDevinBrowser) {
      initializeDevinBrowser();
      return;
    }

    switch (browser) {
      case "firefox":
        WebDriverManager.firefoxdriver().setup();
        driver = new FirefoxDriver();
        break;
      case "edge":
        WebDriverManager.edgedriver().setup();
        driver = new EdgeDriver();
        break;
      case "chrome":
      default:
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        if (Boolean.parseBoolean(config.getProperty("headless", "false"))) {
          // Basic headless setup
          options.addArguments("--headless=new"); // Use new headless mode for better performance
          options.addArguments("--no-sandbox");
          options.addArguments("--disable-dev-shm-usage");
          options.addArguments("--disable-gpu");
          options.addArguments("--window-size=1920,1080");

          // Performance optimizations for faster test execution
          options.addArguments("--disable-extensions");
          options.addArguments("--disable-plugins");
          options.addArguments("--disable-images"); // Don't load images for faster page loads
          options.addArguments("--disable-web-security");
          options.addArguments("--allow-running-insecure-content");
          options.addArguments("--disable-background-timer-throttling");
          options.addArguments("--disable-renderer-backgrounding");
          options.addArguments("--disable-backgrounding-occluded-windows");
          options.addArguments("--disable-client-side-phishing-detection");
          options.addArguments("--disable-sync");
          options.addArguments("--disable-translate");
          options.addArguments("--hide-scrollbars");
          options.addArguments("--metrics-recording-only");
          options.addArguments("--mute-audio");
          options.addArguments("--no-first-run");
          options.addArguments("--safebrowsing-disable-auto-update");
          options.addArguments("--ignore-ssl-errors");
          options.addArguments("--ignore-certificate-errors");
          options.addArguments("--allow-running-insecure-content");
          options.addArguments("--disable-blink-features=AutomationControlled");

          // Memory and resource optimizations
          options.addArguments("--memory-pressure-off");
          options.addArguments("--max_old_space_size=4096");

          // Set preferences for faster execution
          options.setExperimentalOption("useAutomationExtension", false);
          options.setExperimentalOption("excludeSwitches", new String[] {"enable-automation"});
        }
        driver = new ChromeDriver(options);
        break;
    }
  }

  /**
   * Initialize WebDriver to connect to Devin's Chrome browser instance. Devin runs Chrome with
   * specific automation flags and exposes it via remote debugging.
   */
  private void initializeDevinBrowser() {
    try {
      int browserPort = getDevinBrowserPort();

      ChromeOptions options = new ChromeOptions();

      // Connect to existing Chrome instance via remote debugging
      options.setExperimentalOption("debuggerAddress", "127.0.0.1:" + browserPort);

      // Match Devin's user agent for consistency
      options.addArguments(
          "--user-agent=Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 "
              + "(KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36; Devin/1.0; +devin.ai");

      WebDriverManager.chromedriver().setup();
      driver = new ChromeDriver(options);

      // Set viewport to match Devin's configuration
      driver.manage().window().setSize(new Dimension(1550, 1122));

    } catch (Exception e) {
      throw new RuntimeException("Failed to connect to Devin's browser: " + e.getMessage(), e);
    }
  }

  /**
   * Get the browser port from Devin's browser service. Devin's browser runs on ports starting from
   * 32500.
   */
  private int getDevinBrowserPort() throws IOException {
    String devinBrowserServiceUrl =
        config.getProperty(
            "devin.browser.service.url", "http://localhost:3000/browser/start_browser");

    URL url = new URL(devinBrowserServiceUrl);
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    conn.setRequestMethod("POST");
    conn.setRequestProperty("Content-Type", "application/json");
    conn.setDoOutput(true);

    // Send request body
    String jsonBody = "{\"frpConfig\": null}";
    try (OutputStream os = conn.getOutputStream()) {
      os.write(jsonBody.getBytes());
    }

    if (conn.getResponseCode() != 200) {
      throw new IOException("Failed to get browser port. Response code: " + conn.getResponseCode());
    }

    // Parse response to get port
    StringBuilder response = new StringBuilder();
    try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
      String line;
      while ((line = br.readLine()) != null) {
        response.append(line);
      }
    }

    // Simple JSON parsing for port (avoid adding JSON library dependency)
    String responseStr = response.toString();
    int portIndex = responseStr.indexOf("\"port\":");
    if (portIndex == -1) {
      throw new IOException("Port not found in response: " + responseStr);
    }

    String portStr = responseStr.substring(portIndex + 7).split("[,}]")[0].trim();
    return Integer.parseInt(portStr);
  }

  protected void captureScreenshot(String testName) {
    try {
      TakesScreenshot ts = (TakesScreenshot) driver;
      byte[] screenshot = ts.getScreenshotAs(OutputType.BYTES);
      String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
      Path destination = Paths.get(SCREENSHOT_PATH + testName + "_" + timestamp + ".png");
      Files.write(destination, screenshot);
      test.addScreenCaptureFromPath(destination.toString());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  protected ExtentTest createTest(String testName, String description) {
    test = extent.createTest(testName, description);
    return test;
  }
}

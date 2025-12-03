package io.spring.selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class ErrorPage extends BasePage {

  private static final String BASE_URL = "http://localhost:3000";

  @FindBy(css = ".error-message")
  private WebElement errorMessage;

  @FindBy(css = "h1")
  private WebElement pageHeading;

  @FindBy(css = "body")
  private WebElement body;

  public ErrorPage(WebDriver driver) {
    super(driver);
  }

  public void navigateTo(String path) {
    driver.get(BASE_URL + path);
  }

  public String getPageTitle() {
    return driver.getTitle();
  }

  public String getCurrentUrl() {
    return driver.getCurrentUrl();
  }

  public String getPageSource() {
    return driver.getPageSource();
  }

  public boolean isErrorDisplayed() {
    try {
      WebElement error =
          wait.until(
              ExpectedConditions.presenceOfElementLocated(
                  By.cssSelector(".error-message, .error, [class*='error'], [class*='Error']")));
      return error.isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public String getErrorText() {
    try {
      WebElement error =
          wait.until(
              ExpectedConditions.presenceOfElementLocated(
                  By.cssSelector(".error-message, .error, [class*='error'], [class*='Error']")));
      return error.getText();
    } catch (Exception e) {
      return "";
    }
  }

  public boolean is404Page() {
    String pageSource = driver.getPageSource().toLowerCase();
    String title = driver.getTitle().toLowerCase();
    return pageSource.contains("404")
        || pageSource.contains("not found")
        || title.contains("404")
        || title.contains("not found");
  }

  public boolean hasErrorMessage() {
    String pageSource = driver.getPageSource().toLowerCase();
    return pageSource.contains("error")
        || pageSource.contains("not found")
        || pageSource.contains("404")
        || pageSource.contains("can't load")
        || pageSource.contains("cannot");
  }

  public boolean containsText(String text) {
    return driver.getPageSource().toLowerCase().contains(text.toLowerCase());
  }

  public boolean doesNotContainSqlError() {
    String pageSource = driver.getPageSource().toLowerCase();
    return !pageSource.contains("sql")
        && !pageSource.contains("syntax error")
        && !pageSource.contains("database error")
        && !pageSource.contains("query error");
  }

  public boolean doesNotContainXssExecution() {
    String pageSource = driver.getPageSource();
    return !pageSource.contains("<script>alert") && !pageSource.contains("javascript:");
  }

  public void waitForPageLoad() {
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  public long measureResponseTime(String path) {
    long startTime = System.currentTimeMillis();
    driver.get(BASE_URL + path);
    waitForPageLoad();
    return System.currentTimeMillis() - startTime;
  }
}

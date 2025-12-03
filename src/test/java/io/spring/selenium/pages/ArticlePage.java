package io.spring.selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class ArticlePage extends BasePage {

  private static final String BASE_URL = "http://localhost:3000";

  @FindBy(css = ".article-page h1")
  private WebElement articleTitle;

  @FindBy(css = ".article-page .banner")
  private WebElement articleBanner;

  @FindBy(css = ".article-content")
  private WebElement articleContent;

  @FindBy(css = ".error-message")
  private WebElement errorMessage;

  @FindBy(css = ".tag-list")
  private WebElement tagList;

  public ArticlePage(WebDriver driver) {
    super(driver);
  }

  public void navigateToArticle(String slug) {
    driver.get(BASE_URL + "/article/" + slug);
  }

  public void navigateToArticleWithEncodedSlug(String encodedSlug) {
    driver.get(BASE_URL + "/article/" + encodedSlug);
  }

  public String getPageTitle() {
    return driver.getTitle();
  }

  public String getCurrentUrl() {
    return driver.getCurrentUrl();
  }

  public boolean isArticleDisplayed() {
    try {
      return waitForVisibility(articleBanner).isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public boolean isErrorMessageDisplayed() {
    try {
      WebElement error =
          wait.until(
              ExpectedConditions.presenceOfElementLocated(
                  By.cssSelector(".error-message, .error, [class*='error']")));
      return error.isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public String getErrorMessageText() {
    try {
      WebElement error =
          wait.until(
              ExpectedConditions.presenceOfElementLocated(
                  By.cssSelector(".error-message, .error, [class*='error']")));
      return error.getText();
    } catch (Exception e) {
      return "";
    }
  }

  public String getArticleTitle() {
    try {
      return getText(articleTitle);
    } catch (Exception e) {
      return "";
    }
  }

  public boolean isPageNotFound() {
    String pageSource = driver.getPageSource().toLowerCase();
    String currentUrl = driver.getCurrentUrl().toLowerCase();
    return pageSource.contains("not found")
        || pageSource.contains("404")
        || pageSource.contains("error")
        || pageSource.contains("can't load")
        || currentUrl.contains("404")
        || currentUrl.contains("error");
  }

  public boolean hasErrorInPageSource() {
    String pageSource = driver.getPageSource().toLowerCase();
    return pageSource.contains("error")
        || pageSource.contains("not found")
        || pageSource.contains("404")
        || pageSource.contains("cannot read")
        || pageSource.contains("undefined");
  }

  public void waitForPageLoad() {
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }
}

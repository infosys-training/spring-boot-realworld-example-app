package io.spring.selenium.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class ArticlePage extends BasePage {

  @FindBy(css = "h1")
  private WebElement articleTitle;

  @FindBy(css = ".article-content p, .article-body p")
  private WebElement articleBody;

  @FindBy(css = ".author, .article-meta a")
  private WebElement authorLink;

  @FindBy(css = "a[href*='/editor/']")
  private WebElement editButton;

  @FindBy(css = "button.btn-outline-danger, button[class*='delete']")
  private WebElement deleteButton;

  @FindBy(css = ".tag-list .tag-pill, .tag-list span")
  private WebElement tagPill;

  @FindBy(css = ".error-messages li, .error-message, .not-found")
  private WebElement errorMessage;

  @FindBy(css = "button.btn-outline-primary[class*='favorite'], button[class*='favorite']")
  private WebElement favoriteButton;

  @FindBy(css = "button.btn-outline-secondary[class*='follow'], button[class*='follow']")
  private WebElement followButton;

  public ArticlePage(WebDriver driver) {
    super(driver);
  }

  public void navigateTo(String baseUrl, String slug) {
    driver.get(baseUrl + "/article/" + slug);
  }

  public String getArticleTitle() {
    return getText(articleTitle);
  }

  public String getArticleBody() {
    return getText(articleBody);
  }

  public String getCurrentUrl() {
    return driver.getCurrentUrl();
  }

  public String getSlugFromUrl() {
    String url = getCurrentUrl();
    if (url.contains("/article/")) {
      return url.substring(url.lastIndexOf("/article/") + 9);
    }
    return "";
  }

  public boolean isSlugInUrl(String expectedSlugPart) {
    String slug = getSlugFromUrl();
    return slug.toLowerCase().contains(expectedSlugPart.toLowerCase());
  }

  public boolean isSlugLowercase() {
    String slug = getSlugFromUrl();
    return slug.equals(slug.toLowerCase());
  }

  public boolean isSlugUrlSafe() {
    String slug = getSlugFromUrl();
    return slug.matches("^[a-z0-9-]+$");
  }

  public boolean hasUniqueIdentifierSuffix() {
    String slug = getSlugFromUrl();
    String[] parts = slug.split("-");
    if (parts.length > 0) {
      String lastPart = parts[parts.length - 1];
      return lastPart.length() >= 6;
    }
    return false;
  }

  public void clickEdit() {
    click(editButton);
  }

  public void clickDelete() {
    click(deleteButton);
  }

  public boolean isEditButtonDisplayed() {
    return isDisplayed(editButton);
  }

  public boolean isDeleteButtonDisplayed() {
    return isDisplayed(deleteButton);
  }

  public boolean isArticleDisplayed() {
    try {
      return isDisplayed(articleTitle);
    } catch (Exception e) {
      return false;
    }
  }

  public boolean isErrorDisplayed() {
    try {
      return isDisplayed(errorMessage);
    } catch (Exception e) {
      return false;
    }
  }

  public String getErrorMessage() {
    return getText(errorMessage);
  }

  public boolean is404Error() {
    String pageSource = driver.getPageSource().toLowerCase();
    return pageSource.contains("404") || pageSource.contains("not found");
  }

  public void waitForArticleLoad() {
    wait.until(ExpectedConditions.visibilityOf(articleTitle));
  }

  public void waitForUrlContains(String urlPart) {
    wait.until(ExpectedConditions.urlContains(urlPart));
  }
}

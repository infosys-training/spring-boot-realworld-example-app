package io.spring.selenium.pages;

import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class HomePage extends BasePage {

  @FindBy(css = ".article-preview")
  private List<WebElement> articlePreviews;

  @FindBy(css = ".banner h1")
  private WebElement bannerTitle;

  @FindBy(css = ".nav-link[href='/editor/new']")
  private WebElement newArticleLink;

  @FindBy(css = ".nav-link[href='/user/login']")
  private WebElement signInLink;

  @FindBy(css = ".sidebar .tag-list")
  private WebElement popularTags;

  @FindBy(css = ".feed-toggle")
  private WebElement feedToggle;

  public HomePage(WebDriver driver) {
    super(driver);
  }

  public HomePage navigateTo(String baseUrl) {
    driver.get(baseUrl);
    return this;
  }

  public boolean isOnHomePage() {
    try {
      return waitForVisibility(bannerTitle).isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public int getArticleCount() {
    try {
      wait.until(ExpectedConditions.visibilityOfAllElements(articlePreviews));
      return articlePreviews.size();
    } catch (Exception e) {
      return 0;
    }
  }

  public List<WebElement> getArticlePreviews() {
    wait.until(ExpectedConditions.visibilityOfAllElements(articlePreviews));
    return articlePreviews;
  }

  public String getArticleTimestamp(int index) {
    if (index >= 0 && index < articlePreviews.size()) {
      WebElement article = articlePreviews.get(index);
      WebElement dateElement = article.findElement(By.cssSelector(".date"));
      return dateElement.getText();
    }
    return null;
  }

  public void clickArticle(int index) {
    if (index >= 0 && index < articlePreviews.size()) {
      WebElement article = articlePreviews.get(index);
      WebElement link = article.findElement(By.cssSelector(".preview-link"));
      click(link);
    }
  }

  public void clickNewArticle() {
    click(newArticleLink);
  }

  public void clickSignIn() {
    click(signInLink);
  }

  public boolean isUserLoggedIn() {
    try {
      return isDisplayed(newArticleLink);
    } catch (Exception e) {
      return false;
    }
  }

  public void waitForArticlesToLoad() {
    wait.until(ExpectedConditions.visibilityOfAllElements(articlePreviews));
  }
}

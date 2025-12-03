package io.spring.selenium.pages;

import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

/** Page Object for the Home page. */
public class HomePage extends BasePage {

  @FindBy(css = ".navbar-brand")
  private WebElement navbarBrand;

  @FindBy(css = "a[href='/user/login']")
  private WebElement signInLink;

  @FindBy(css = "a[href='/user/register']")
  private WebElement signUpLink;

  @FindBy(css = "a[href='/editor/new']")
  private WebElement newArticleLink;

  @FindBy(css = "a[href='/user/settings']")
  private WebElement settingsLink;

  @FindBy(css = ".article-preview")
  private List<WebElement> articlePreviews;

  @FindBy(css = ".nav-pills .nav-link")
  private List<WebElement> feedTabs;

  @FindBy(css = ".tag-list .tag-pill")
  private List<WebElement> popularTags;

  @FindBy(css = ".pagination .page-item")
  private List<WebElement> paginationItems;

  public HomePage(WebDriver driver) {
    super(driver);
  }

  public void navigateTo(String baseUrl) {
    driver.get(baseUrl);
  }

  public boolean isLoggedIn() {
    try {
      return isDisplayed(newArticleLink);
    } catch (Exception e) {
      return false;
    }
  }

  public boolean isLoggedOut() {
    try {
      return isDisplayed(signInLink);
    } catch (Exception e) {
      return false;
    }
  }

  public void clickSignIn() {
    click(signInLink);
  }

  public void clickNewArticle() {
    click(newArticleLink);
  }

  public void clickSettings() {
    click(settingsLink);
  }

  public int getArticleCount() {
    return articlePreviews.size();
  }

  public void clickArticleByIndex(int index) {
    if (index < articlePreviews.size()) {
      WebElement article = articlePreviews.get(index);
      WebElement titleLink = article.findElement(By.cssSelector("a.preview-link h1"));
      click(titleLink);
    }
  }

  public void clickArticleByTitle(String title) {
    for (WebElement article : articlePreviews) {
      WebElement titleElement = article.findElement(By.cssSelector("a.preview-link h1"));
      if (titleElement.getText().equals(title)) {
        click(titleElement);
        return;
      }
    }
  }

  public String getArticleTitleByIndex(int index) {
    if (index < articlePreviews.size()) {
      WebElement article = articlePreviews.get(index);
      WebElement titleElement = article.findElement(By.cssSelector("a.preview-link h1"));
      return getText(titleElement);
    }
    return "";
  }

  public void clickYourFeed() {
    for (WebElement tab : feedTabs) {
      if (tab.getText().contains("Your Feed")) {
        click(tab);
        return;
      }
    }
  }

  public void clickGlobalFeed() {
    for (WebElement tab : feedTabs) {
      if (tab.getText().contains("Global Feed")) {
        click(tab);
        return;
      }
    }
  }

  public void clickTag(String tagName) {
    for (WebElement tag : popularTags) {
      if (tag.getText().equals(tagName)) {
        click(tag);
        return;
      }
    }
  }

  public void waitForArticlesToLoad() {
    wait.until(
        ExpectedConditions.or(
            ExpectedConditions.visibilityOfAllElements(articlePreviews),
            ExpectedConditions.presenceOfElementLocated(By.cssSelector(".article-preview"))));
  }

  public String getCurrentUrl() {
    return driver.getCurrentUrl();
  }

  public String getPageTitle() {
    return driver.getTitle();
  }
}

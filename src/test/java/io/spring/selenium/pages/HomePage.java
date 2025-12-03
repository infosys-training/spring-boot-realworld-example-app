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

  @FindBy(css = "a[href='/editor/new']")
  private WebElement newArticleLink;

  @FindBy(css = "a[href='/user/settings']")
  private WebElement settingsLink;

  @FindBy(css = "a[href='/user/login']")
  private WebElement signInLink;

  @FindBy(css = "a[href='/user/register']")
  private WebElement signUpLink;

  @FindBy(css = ".article-preview")
  private List<WebElement> articlePreviews;

  @FindBy(css = ".nav-pills .nav-link")
  private List<WebElement> feedTabs;

  @FindBy(css = ".tag-list .tag-pill")
  private List<WebElement> popularTags;

  @FindBy(css = ".pagination .page-item")
  private List<WebElement> paginationItems;

  @FindBy(css = ".article-preview h1")
  private List<WebElement> articleTitles;

  @FindBy(css = ".article-preview .author")
  private List<WebElement> articleAuthors;

  public HomePage(WebDriver driver) {
    super(driver);
  }

  public void navigateTo() {
    driver.get(getBaseUrl());
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

  public void clickNewArticle() {
    click(newArticleLink);
  }

  public void clickSettings() {
    click(settingsLink);
  }

  public void clickSignIn() {
    click(signInLink);
  }

  public void clickSignUp() {
    click(signUpLink);
  }

  public int getArticleCount() {
    return articlePreviews.size();
  }

  public void clickArticleByTitle(String title) {
    for (WebElement preview : articlePreviews) {
      WebElement titleElement = preview.findElement(By.cssSelector("h1"));
      if (titleElement.getText().equals(title)) {
        click(titleElement);
        return;
      }
    }
    throw new RuntimeException("Article with title '" + title + "' not found");
  }

  public void clickArticleByIndex(int index) {
    if (index >= 0 && index < articleTitles.size()) {
      click(articleTitles.get(index));
    } else {
      throw new IndexOutOfBoundsException("Article index " + index + " out of bounds");
    }
  }

  public boolean isArticleVisible(String title) {
    for (WebElement titleElement : articleTitles) {
      if (titleElement.getText().equals(title)) {
        return true;
      }
    }
    return false;
  }

  public void clickGlobalFeed() {
    for (WebElement tab : feedTabs) {
      if (tab.getText().contains("Global Feed")) {
        click(tab);
        return;
      }
    }
  }

  public void clickYourFeed() {
    for (WebElement tab : feedTabs) {
      if (tab.getText().contains("Your Feed")) {
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
            ExpectedConditions.textToBePresentInElementLocated(
                By.cssSelector(".article-preview"), "No articles")));
  }

  public String getFirstArticleTitle() {
    if (!articleTitles.isEmpty()) {
      return getText(articleTitles.get(0));
    }
    return null;
  }

  public String getFirstArticleAuthor() {
    if (!articleAuthors.isEmpty()) {
      return getText(articleAuthors.get(0));
    }
    return null;
  }

  public void clickUserProfile(String username) {
    WebElement profileLink =
        driver.findElement(By.cssSelector("a[href*='/profile/" + username + "']"));
    click(profileLink);
  }

  private String getBaseUrl() {
    return System.getProperty("base.url", "http://localhost:3000");
  }
}

package io.spring.selenium.pages;

import java.util.List;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class HomePage extends BasePage {

  @FindBy(css = ".navbar-brand")
  private WebElement navbarBrand;

  @FindBy(css = "a[href='/user/login']")
  private WebElement signInLink;

  @FindBy(css = "a[href='/user/register']")
  private WebElement signUpLink;

  @FindBy(css = "a[href='/editor']")
  private WebElement newArticleLink;

  @FindBy(css = "a[href='/settings']")
  private WebElement settingsLink;

  @FindBy(css = ".nav-link.active")
  private WebElement activeNavLink;

  @FindBy(css = ".article-preview")
  private List<WebElement> articlePreviews;

  @FindBy(css = ".article-preview h1")
  private List<WebElement> articleTitles;

  @FindBy(css = ".tag-list .tag-pill")
  private List<WebElement> popularTags;

  @FindBy(css = ".feed-toggle .nav-link")
  private List<WebElement> feedTabs;

  @FindBy(css = ".pagination .page-item")
  private List<WebElement> paginationItems;

  public HomePage(WebDriver driver) {
    super(driver);
  }

  public HomePage navigateTo(String baseUrl) {
    driver.get(baseUrl);
    waitForPageLoad();
    return this;
  }

  public LoginPage clickSignIn() {
    click(signInLink);
    return new LoginPage(driver);
  }

  public boolean isLoggedIn() {
    try {
      return newArticleLink.isDisplayed() || settingsLink.isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public boolean isSignInLinkVisible() {
    try {
      return signInLink.isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public String getLoggedInUsername() {
    try {
      List<WebElement> profileLinks =
          driver.findElements(org.openqa.selenium.By.cssSelector(".nav-link[href^='/profile/']"));
      if (!profileLinks.isEmpty()) {
        return profileLinks.get(0).getText().trim();
      }
    } catch (Exception e) {
      // ignore
    }
    return "";
  }

  public ArticlePage clickArticle(int index) {
    if (index < articleTitles.size()) {
      click(articleTitles.get(index));
      return new ArticlePage(driver);
    }
    throw new IndexOutOfBoundsException("Article index out of bounds: " + index);
  }

  public ArticlePage clickArticleByTitle(String title) {
    for (WebElement articleTitle : articleTitles) {
      if (articleTitle.getText().contains(title)) {
        click(articleTitle);
        return new ArticlePage(driver);
      }
    }
    throw new RuntimeException("Article not found with title: " + title);
  }

  public int getArticleCount() {
    return articlePreviews.size();
  }

  public void clickTag(String tagName) {
    for (WebElement tag : popularTags) {
      if (tag.getText().equals(tagName)) {
        click(tag);
        return;
      }
    }
    throw new RuntimeException("Tag not found: " + tagName);
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

  private void waitForPageLoad() {
    wait.until(ExpectedConditions.visibilityOf(navbarBrand));
  }

  public void waitForArticlesToLoad() {
    wait.until(
        ExpectedConditions.or(
            ExpectedConditions.visibilityOfAllElements(articlePreviews),
            ExpectedConditions.textToBePresentInElementLocated(
                org.openqa.selenium.By.cssSelector(".article-preview"), "No articles")));
  }
}

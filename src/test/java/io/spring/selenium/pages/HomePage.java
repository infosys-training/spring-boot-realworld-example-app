package io.spring.selenium.pages;

import java.util.List;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

/** Page Object for the Home page with feed functionality. */
public class HomePage extends BasePage {

  private static final String HOME_URL = "http://localhost:3000";

  @FindBy(css = ".navbar-brand")
  private WebElement navbarBrand;

  @FindBy(css = ".nav-link[href='/']")
  private WebElement homeLink;

  @FindBy(css = ".nav-link[href='/user/login']")
  private WebElement signInLink;

  @FindBy(css = ".nav-link[href='/user/register']")
  private WebElement signUpLink;

  @FindBy(css = ".feed-toggle .nav-link")
  private List<WebElement> feedTabs;

  @FindBy(css = ".feed-toggle .nav-link.active")
  private WebElement activeFeedTab;

  @FindBy(css = ".article-preview")
  private List<WebElement> articlePreviews;

  @FindBy(css = ".article-preview .author")
  private List<WebElement> articleAuthors;

  @FindBy(css = ".tag-list .tag-pill")
  private List<WebElement> popularTags;

  @FindBy(css = ".sidebar .tag-list")
  private WebElement tagsSidebar;

  @FindBy(css = ".nav-link[href='/editor']")
  private WebElement newArticleLink;

  @FindBy(css = ".nav-link[href='/settings']")
  private WebElement settingsLink;

  @FindBy(css = ".user-pic")
  private WebElement userPic;

  @FindBy(css = ".pagination")
  private WebElement pagination;

  public HomePage(WebDriver driver) {
    super(driver);
  }

  public void navigateTo() {
    driver.get(HOME_URL);
    waitForPageLoad();
  }

  public void waitForPageLoad() {
    wait.until(ExpectedConditions.visibilityOf(navbarBrand));
  }

  public boolean isLoggedIn() {
    try {
      return userPic.isDisplayed() || newArticleLink.isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public boolean isLoggedOut() {
    try {
      return signInLink.isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public void clickSignIn() {
    click(signInLink);
  }

  public void clickSignUp() {
    click(signUpLink);
  }

  public void clickYourFeed() {
    for (WebElement tab : feedTabs) {
      if (tab.getText().contains("Your Feed")) {
        click(tab);
        break;
      }
    }
  }

  public void clickGlobalFeed() {
    for (WebElement tab : feedTabs) {
      if (tab.getText().contains("Global Feed")) {
        click(tab);
        break;
      }
    }
  }

  public boolean isYourFeedActive() {
    try {
      return activeFeedTab.getText().contains("Your Feed");
    } catch (Exception e) {
      return false;
    }
  }

  public boolean isGlobalFeedActive() {
    try {
      return activeFeedTab.getText().contains("Global Feed");
    } catch (Exception e) {
      return false;
    }
  }

  public int getArticleCount() {
    return articlePreviews.size();
  }

  public boolean hasArticles() {
    return getArticleCount() > 0;
  }

  public void clickFirstArticleAuthor() {
    if (!articleAuthors.isEmpty()) {
      click(articleAuthors.get(0));
    }
  }

  public String getFirstArticleAuthorName() {
    if (!articleAuthors.isEmpty()) {
      return getText(articleAuthors.get(0));
    }
    return "";
  }

  public void clickArticleAuthor(int index) {
    if (index < articleAuthors.size()) {
      click(articleAuthors.get(index));
    }
  }

  public void clickTag(String tagName) {
    for (WebElement tag : popularTags) {
      if (tag.getText().equals(tagName)) {
        click(tag);
        break;
      }
    }
  }

  public boolean hasYourFeedTab() {
    for (WebElement tab : feedTabs) {
      if (tab.getText().contains("Your Feed")) {
        return true;
      }
    }
    return false;
  }

  public void clickUserProfile() {
    click(userPic);
  }

  public void clickSettings() {
    click(settingsLink);
  }

  public void clickNewArticle() {
    click(newArticleLink);
  }

  public String getCurrentUrl() {
    return driver.getCurrentUrl();
  }

  public void refreshPage() {
    driver.navigate().refresh();
    waitForPageLoad();
  }

  public boolean hasFeedArticlesFromFollowedUsers() {
    return hasArticles();
  }

  public List<WebElement> getArticlePreviews() {
    return articlePreviews;
  }

  public void waitForFeedToLoad() {
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }
}

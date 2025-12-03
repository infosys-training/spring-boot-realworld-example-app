package io.spring.selenium.pages;

import java.util.List;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class HomePage extends BasePage {

  @FindBy(css = "a.navbar-brand")
  private WebElement conduitLogo;

  @FindBy(linkText = "Home")
  private WebElement homeLink;

  @FindBy(linkText = "Sign in")
  private WebElement signInLink;

  @FindBy(linkText = "Sign up")
  private WebElement signUpLink;

  @FindBy(linkText = "New Article")
  private WebElement newArticleLink;

  @FindBy(linkText = "Settings")
  private WebElement settingsLink;

  @FindBy(css = "a[href*='/profile/']")
  private WebElement profileLink;

  @FindBy(css = ".nav-pills .nav-link")
  private List<WebElement> feedTabs;

  @FindBy(css = ".article-preview")
  private List<WebElement> articlePreviews;

  @FindBy(css = ".tag-list .tag-pill")
  private List<WebElement> popularTags;

  @FindBy(css = ".feed-toggle .nav-link.active")
  private WebElement activeFeedTab;

  @FindBy(linkText = "Your Feed")
  private WebElement yourFeedTab;

  @FindBy(linkText = "Global Feed")
  private WebElement globalFeedTab;

  public HomePage(WebDriver driver) {
    super(driver);
  }

  public void navigateToHomePage(String baseUrl) {
    driver.get(baseUrl);
  }

  public boolean isUserLoggedIn() {
    try {
      return newArticleLink.isDisplayed() && settingsLink.isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public boolean isUserLoggedOut() {
    try {
      return signInLink.isDisplayed() && signUpLink.isDisplayed();
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

  public void clickNewArticle() {
    click(newArticleLink);
  }

  public void clickSettings() {
    click(settingsLink);
  }

  public void clickProfile() {
    click(profileLink);
  }

  public void clickYourFeed() {
    click(yourFeedTab);
  }

  public void clickGlobalFeed() {
    click(globalFeedTab);
  }

  public String getActiveTabText() {
    return getText(activeFeedTab);
  }

  public boolean isYourFeedDisplayed() {
    try {
      return yourFeedTab.isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public int getArticleCount() {
    return articlePreviews.size();
  }

  public String getLoggedInUsername() {
    try {
      return getText(profileLink);
    } catch (Exception e) {
      return null;
    }
  }

  public boolean isNewArticleLinkDisplayed() {
    try {
      return newArticleLink.isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public boolean isSettingsLinkDisplayed() {
    try {
      return settingsLink.isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public void clickFirstArticle() {
    if (!articlePreviews.isEmpty()) {
      click(
          articlePreviews.get(0).findElement(org.openqa.selenium.By.cssSelector("a.preview-link")));
    }
  }

  public String getCurrentUrl() {
    return driver.getCurrentUrl();
  }

  public String getPageTitle() {
    return driver.getTitle();
  }
}

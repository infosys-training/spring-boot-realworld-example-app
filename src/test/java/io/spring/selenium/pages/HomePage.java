package io.spring.selenium.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class HomePage extends BasePage {

  @FindBy(css = ".navbar-brand")
  private WebElement navbarBrand;

  @FindBy(linkText = "Sign in")
  private WebElement signInLink;

  @FindBy(linkText = "Sign up")
  private WebElement signUpLink;

  @FindBy(linkText = "Settings")
  private WebElement settingsLink;

  @FindBy(linkText = "New Article")
  private WebElement newArticleLink;

  @FindBy(css = ".nav-link[href*='profile']")
  private WebElement profileLink;

  @FindBy(css = ".feed-toggle")
  private WebElement feedToggle;

  @FindBy(css = ".article-preview")
  private WebElement articlePreview;

  @FindBy(css = ".banner h1")
  private WebElement bannerTitle;

  public HomePage(WebDriver driver) {
    super(driver);
  }

  public void navigateTo(String baseUrl) {
    driver.get(baseUrl);
  }

  public boolean isUserLoggedIn() {
    try {
      return isDisplayed(settingsLink);
    } catch (Exception e) {
      return false;
    }
  }

  public boolean isUserLoggedOut() {
    try {
      return isDisplayed(signInLink) && isDisplayed(signUpLink);
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

  public void clickSettings() {
    click(settingsLink);
  }

  public void clickNewArticle() {
    click(newArticleLink);
  }

  public void clickProfile() {
    click(profileLink);
  }

  public boolean isPageLoaded() {
    try {
      return waitForVisibility(navbarBrand).isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public String getNavbarBrandText() {
    return getText(navbarBrand);
  }

  public boolean isSettingsLinkVisible() {
    return isDisplayed(settingsLink);
  }

  public boolean isNewArticleLinkVisible() {
    return isDisplayed(newArticleLink);
  }

  public boolean isProfileLinkVisible() {
    return isDisplayed(profileLink);
  }

  public String getCurrentUrl() {
    return driver.getCurrentUrl();
  }

  public String getPageTitle() {
    return driver.getTitle();
  }
}

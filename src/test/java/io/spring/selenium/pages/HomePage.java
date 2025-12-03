package io.spring.selenium.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/** Page Object for the Home page. */
public class HomePage extends BasePage {

  @FindBy(css = ".banner h1")
  private WebElement bannerTitle;

  @FindBy(css = ".navbar-brand")
  private WebElement navbarBrand;

  @FindBy(css = "a[href='/user/register']")
  private WebElement signUpLink;

  @FindBy(css = "a[href='/user/login']")
  private WebElement signInLink;

  @FindBy(css = "a[href='/user/settings']")
  private WebElement settingsLink;

  @FindBy(css = ".nav-link.active")
  private WebElement activeNavLink;

  @FindBy(css = ".home-page")
  private WebElement homePageContainer;

  @FindBy(css = ".sidebar")
  private WebElement sidebar;

  @FindBy(css = ".article-preview")
  private WebElement articlePreview;

  public HomePage(WebDriver driver) {
    super(driver);
  }

  public HomePage navigateTo(String baseUrl) {
    driver.get(baseUrl);
    waitForPageLoad();
    return this;
  }

  public void waitForPageLoad() {
    waitForVisibility(homePageContainer);
  }

  public String getBannerTitle() {
    try {
      return getText(bannerTitle);
    } catch (Exception e) {
      return "";
    }
  }

  public boolean isOnHomePage() {
    try {
      return isDisplayed(homePageContainer);
    } catch (Exception e) {
      return false;
    }
  }

  public boolean isSignUpLinkDisplayed() {
    try {
      return isDisplayed(signUpLink);
    } catch (Exception e) {
      return false;
    }
  }

  public boolean isSignInLinkDisplayed() {
    try {
      return isDisplayed(signInLink);
    } catch (Exception e) {
      return false;
    }
  }

  public boolean isSettingsLinkDisplayed() {
    try {
      return isDisplayed(settingsLink);
    } catch (Exception e) {
      return false;
    }
  }

  public void clickSignUpLink() {
    click(signUpLink);
  }

  public void clickSignInLink() {
    click(signInLink);
  }

  public String getNavbarBrandText() {
    return getText(navbarBrand);
  }

  public boolean isUserLoggedIn() {
    return isSettingsLinkDisplayed() && !isSignUpLinkDisplayed();
  }

  public String getCurrentUrl() {
    return driver.getCurrentUrl();
  }

  public String getPageTitle() {
    return driver.getTitle();
  }
}

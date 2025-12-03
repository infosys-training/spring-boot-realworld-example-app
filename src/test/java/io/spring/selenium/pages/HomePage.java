package io.spring.selenium.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

/** Page Object for the Home page. */
public class HomePage extends BasePage {

  @FindBy(css = ".navbar-brand")
  private WebElement navbarBrand;

  @FindBy(linkText = "Sign in")
  private WebElement signInLink;

  @FindBy(linkText = "Sign up")
  private WebElement signUpLink;

  @FindBy(linkText = "Settings")
  private WebElement settingsLink;

  @FindBy(css = ".nav-link[href='/user/settings']")
  private WebElement settingsNavLink;

  @FindBy(css = "a.nav-link[href='/']")
  private WebElement homeLink;

  @FindBy(css = ".feed-toggle")
  private WebElement feedToggle;

  @FindBy(css = ".article-preview")
  private WebElement articlePreview;

  public HomePage(WebDriver driver) {
    super(driver);
  }

  public void navigateTo() {
    driver.get(getBaseUrl());
  }

  public void clickSignIn() {
    click(signInLink);
  }

  public void clickSignUp() {
    click(signUpLink);
  }

  public void clickSettings() {
    try {
      click(settingsNavLink);
    } catch (Exception e) {
      click(settingsLink);
    }
  }

  public void clickHome() {
    click(homeLink);
  }

  public boolean isUserLoggedIn() {
    try {
      return settingsNavLink.isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public boolean isSignInLinkDisplayed() {
    try {
      return signInLink.isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public boolean isSignUpLinkDisplayed() {
    try {
      return signUpLink.isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public boolean isPageLoaded() {
    try {
      return waitForVisibility(navbarBrand).isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public void waitForHomePageLoad() {
    wait.until(ExpectedConditions.visibilityOf(navbarBrand));
  }

  public String getNavbarBrandText() {
    return getText(navbarBrand);
  }

  public boolean isFeedToggleDisplayed() {
    try {
      return feedToggle.isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  private String getBaseUrl() {
    return System.getProperty("base.url", "http://localhost:3000");
  }
}

package io.spring.selenium.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class HomePage extends BasePage {

  @FindBy(css = "a.navbar-brand")
  private WebElement navbarBrand;

  @FindBy(css = "a[href='/user/register']")
  private WebElement signUpLink;

  @FindBy(css = "a[href='/user/login']")
  private WebElement signInLink;

  @FindBy(css = "a[href='/user/settings']")
  private WebElement settingsLink;

  @FindBy(css = "a[href='/editor']")
  private WebElement newArticleLink;

  @FindBy(css = "div.banner h1")
  private WebElement bannerTitle;

  @FindBy(css = "ul.nav.navbar-nav.pull-xs-right")
  private WebElement navbarRight;

  public HomePage(WebDriver driver) {
    super(driver);
  }

  public HomePage navigateTo(String baseUrl) {
    driver.get(baseUrl);
    waitForPageLoad();
    return this;
  }

  public void waitForPageLoad() {
    waitForVisibility(navbarBrand);
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

  public boolean isNewArticleLinkDisplayed() {
    try {
      return isDisplayed(newArticleLink);
    } catch (Exception e) {
      return false;
    }
  }

  public boolean isUserLoggedIn() {
    return isSettingsLinkDisplayed() && isNewArticleLinkDisplayed();
  }

  public boolean isUserLoggedOut() {
    return isSignUpLinkDisplayed() && isSignInLinkDisplayed();
  }

  public void clickSignUp() {
    click(signUpLink);
  }

  public void clickSignIn() {
    click(signInLink);
  }

  public String getCurrentUrl() {
    return driver.getCurrentUrl();
  }

  public String getNavbarText() {
    return getText(navbarRight);
  }

  public boolean hasJwtTokenInLocalStorage() {
    try {
      Object result =
          ((org.openqa.selenium.JavascriptExecutor) driver)
              .executeScript("return window.localStorage.getItem('user');");
      if (result != null) {
        String userJson = result.toString();
        return userJson.contains("token");
      }
      return false;
    } catch (Exception e) {
      return false;
    }
  }

  public String getLocalStorageUser() {
    try {
      Object result =
          ((org.openqa.selenium.JavascriptExecutor) driver)
              .executeScript("return window.localStorage.getItem('user');");
      return result != null ? result.toString() : "";
    } catch (Exception e) {
      return "";
    }
  }

  public void clearLocalStorage() {
    try {
      ((org.openqa.selenium.JavascriptExecutor) driver)
          .executeScript("window.localStorage.clear();");
    } catch (Exception e) {
      // Ignore errors
    }
  }
}

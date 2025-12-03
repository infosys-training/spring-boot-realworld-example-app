package io.spring.selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class NavigationPage extends BasePage {

  @FindBy(css = "nav.navbar")
  private WebElement navbar;

  @FindBy(css = ".navbar-brand")
  private WebElement brandLink;

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

  @FindBy(css = ".nav-link[href*='/profile/']")
  private WebElement profileLink;

  public NavigationPage(WebDriver driver) {
    super(driver);
  }

  public boolean isNavbarVisible() {
    try {
      return isDisplayed(navbar);
    } catch (Exception e) {
      return false;
    }
  }

  public HomePage clickHome() {
    click(homeLink);
    return new HomePage(driver);
  }

  public HomePage clickBrand() {
    click(brandLink);
    return new HomePage(driver);
  }

  public LoginPage clickSignIn() {
    click(signInLink);
    return new LoginPage(driver);
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

  public boolean isSignInLinkVisible() {
    try {
      return isDisplayed(signInLink);
    } catch (Exception e) {
      return false;
    }
  }

  public boolean isSignUpLinkVisible() {
    try {
      return isDisplayed(signUpLink);
    } catch (Exception e) {
      return false;
    }
  }

  public boolean isNewArticleLinkVisible() {
    try {
      return isDisplayed(newArticleLink);
    } catch (Exception e) {
      return false;
    }
  }

  public boolean isSettingsLinkVisible() {
    try {
      return isDisplayed(settingsLink);
    } catch (Exception e) {
      return false;
    }
  }

  public boolean isProfileLinkVisible() {
    try {
      return isDisplayed(profileLink);
    } catch (Exception e) {
      return false;
    }
  }

  public boolean isLoggedIn() {
    return isNewArticleLinkVisible() && isSettingsLinkVisible() && !isSignInLinkVisible();
  }

  public boolean isLoggedOut() {
    return isSignInLinkVisible() && isSignUpLinkVisible() && !isNewArticleLinkVisible();
  }

  public String getLoggedInUsername() {
    try {
      return getText(profileLink);
    } catch (Exception e) {
      return "";
    }
  }

  public void navigateToProfile(String username) {
    try {
      WebElement userProfileLink =
          driver.findElement(By.cssSelector(".nav-link[href*='/profile/" + username + "']"));
      click(userProfileLink);
    } catch (Exception e) {
      throw new RuntimeException("Could not navigate to profile: " + username, e);
    }
  }
}

package io.spring.selenium.pages;

import java.util.List;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class NavbarComponent extends BasePage {

  @FindBy(css = ".navbar")
  private WebElement navbar;

  @FindBy(css = ".navbar-brand")
  private WebElement navbarBrand;

  @FindBy(linkText = "Home")
  private WebElement homeLink;

  @FindBy(linkText = "Sign in")
  private WebElement signInLink;

  @FindBy(linkText = "Sign up")
  private WebElement signUpLink;

  @FindBy(linkText = "New Post")
  private WebElement newPostLink;

  @FindBy(linkText = "Settings")
  private WebElement settingsLink;

  @FindBy(css = ".nav-item")
  private List<WebElement> navItems;

  public NavbarComponent(WebDriver driver) {
    super(driver);
  }

  public boolean isNavbarDisplayed() {
    return isDisplayed(navbar);
  }

  public boolean isSignInLinkDisplayed() {
    try {
      return isDisplayed(signInLink);
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

  public boolean isNewPostLinkDisplayed() {
    try {
      return isDisplayed(newPostLink);
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

  public boolean isUserLoggedIn() {
    return isSettingsLinkDisplayed() && isNewPostLinkDisplayed();
  }

  public boolean isUserLoggedOut() {
    return isSignInLinkDisplayed() && isSignUpLinkDisplayed();
  }

  public LoginPage clickSignIn() {
    click(signInLink);
    return new LoginPage(driver);
  }

  public void clickSignUp() {
    click(signUpLink);
  }

  public void clickHome() {
    click(homeLink);
  }

  public void clickNewPost() {
    click(newPostLink);
  }

  public SettingsPage clickSettings() {
    click(settingsLink);
    return new SettingsPage(driver);
  }

  public void clickNavbarBrand() {
    click(navbarBrand);
  }

  public void waitForLoggedInState() {
    wait.until(ExpectedConditions.visibilityOf(settingsLink));
  }

  public void waitForLoggedOutState() {
    wait.until(ExpectedConditions.visibilityOf(signInLink));
  }

  public int getNavItemCount() {
    return navItems.size();
  }

  public boolean isUsernameDisplayedInNavbar(String username) {
    try {
      for (WebElement item : navItems) {
        if (item.getText().contains(username)) {
          return true;
        }
      }
      return false;
    } catch (Exception e) {
      return false;
    }
  }
}

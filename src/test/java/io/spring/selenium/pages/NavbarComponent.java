package io.spring.selenium.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class NavbarComponent extends BasePage {

  @FindBy(css = "a.navbar-brand")
  private WebElement homeLink;

  @FindBy(css = "a[href='/login']")
  private WebElement signInLink;

  @FindBy(css = "a[href='/register']")
  private WebElement signUpLink;

  @FindBy(css = "a[href='/settings']")
  private WebElement settingsLink;

  @FindBy(css = "a[href='/editor']")
  private WebElement newArticleLink;

  @FindBy(css = ".nav-link[href*='/profile/']")
  private WebElement profileLink;

  @FindBy(css = "nav.navbar")
  private WebElement navbar;

  public NavbarComponent(WebDriver driver) {
    super(driver);
  }

  public void clickHome() {
    click(homeLink);
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

  public boolean isSignInLinkDisplayed() {
    return isDisplayed(signInLink);
  }

  public boolean isSignUpLinkDisplayed() {
    return isDisplayed(signUpLink);
  }

  public boolean isSettingsLinkDisplayed() {
    return isDisplayed(settingsLink);
  }

  public boolean isNewArticleLinkDisplayed() {
    return isDisplayed(newArticleLink);
  }

  public boolean isProfileLinkDisplayed() {
    return isDisplayed(profileLink);
  }

  public boolean isUserLoggedIn() {
    return isSettingsLinkDisplayed() && isProfileLinkDisplayed();
  }

  public boolean isUserLoggedOut() {
    return isSignInLinkDisplayed() && isSignUpLinkDisplayed();
  }

  public String getProfileLinkText() {
    try {
      return getText(profileLink);
    } catch (Exception e) {
      return "";
    }
  }

  public String getProfileLinkHref() {
    try {
      return profileLink.getAttribute("href");
    } catch (Exception e) {
      return "";
    }
  }

  public boolean isNavbarDisplayed() {
    return isDisplayed(navbar);
  }
}

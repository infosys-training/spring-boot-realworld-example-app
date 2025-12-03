package io.spring.selenium.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class NavigationBar extends BasePage {

  @FindBy(css = "a.navbar-brand")
  private WebElement brandLink;

  @FindBy(css = "a[href='/']")
  private WebElement homeLink;

  @FindBy(css = "a[href='/user/login']")
  private WebElement signInLink;

  @FindBy(css = "a[href='/user/register']")
  private WebElement signUpLink;

  @FindBy(css = "a[href='/editor']")
  private WebElement newArticleLink;

  @FindBy(css = "a[href='/settings']")
  private WebElement settingsLink;

  @FindBy(css = ".nav-link[href*='/profile/']")
  private WebElement profileLink;

  @FindBy(css = ".navbar")
  private WebElement navbar;

  public NavigationBar(WebDriver driver) {
    super(driver);
  }

  public void waitForNavbar() {
    wait.until(ExpectedConditions.visibilityOf(navbar));
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

  public void clickNewArticle() {
    click(newArticleLink);
  }

  public void clickSettings() {
    click(settingsLink);
  }

  public void clickProfile() {
    click(profileLink);
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

  public boolean isNewArticleLinkDisplayed() {
    try {
      return isDisplayed(newArticleLink);
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

  public boolean isProfileLinkDisplayed() {
    try {
      return isDisplayed(profileLink);
    } catch (Exception e) {
      return false;
    }
  }

  public String getProfileUsername() {
    try {
      wait.until(ExpectedConditions.visibilityOf(profileLink));
      String href = profileLink.getAttribute("href");
      return href.substring(href.lastIndexOf("/") + 1);
    } catch (Exception e) {
      return null;
    }
  }

  public boolean isUserLoggedIn() {
    return isSettingsLinkDisplayed() && isNewArticleLinkDisplayed();
  }

  public boolean isUserLoggedOut() {
    return isSignInLinkDisplayed() && isSignUpLinkDisplayed();
  }
}

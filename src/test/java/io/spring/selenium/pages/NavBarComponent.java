package io.spring.selenium.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class NavBarComponent extends BasePage {

  @FindBy(css = ".navbar-brand")
  private WebElement navbarBrand;

  @FindBy(css = "a[href='/']")
  private WebElement homeLink;

  @FindBy(css = "a[href='/user/register']")
  private WebElement signUpLink;

  @FindBy(css = "a[href='/user/login']")
  private WebElement signInLink;

  @FindBy(css = "a[href='/user/settings']")
  private WebElement settingsLink;

  @FindBy(css = "a[href='/editor']")
  private WebElement newArticleLink;

  @FindBy(css = ".nav-link.active")
  private WebElement activeNavLink;

  public NavBarComponent(WebDriver driver) {
    super(driver);
  }

  public boolean isNavbarBrandDisplayed() {
    try {
      return waitForVisibility(navbarBrand).isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public void clickNavbarBrand() {
    click(navbarBrand);
  }

  public void clickHomeLink() {
    click(homeLink);
  }

  public void clickSignUpLink() {
    click(signUpLink);
  }

  public void clickSignInLink() {
    click(signInLink);
  }

  public void clickSettingsLink() {
    click(settingsLink);
  }

  public void clickNewArticleLink() {
    click(newArticleLink);
  }

  public boolean isSignUpLinkDisplayed() {
    try {
      return signUpLink.isDisplayed();
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

  public boolean isSettingsLinkDisplayed() {
    try {
      return settingsLink.isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public boolean isNewArticleLinkDisplayed() {
    try {
      return newArticleLink.isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public boolean isUserLoggedIn() {
    return isSettingsLinkDisplayed() || isNewArticleLinkDisplayed();
  }

  public boolean isUserLoggedOut() {
    return isSignUpLinkDisplayed() && isSignInLinkDisplayed();
  }

  public String getActiveNavLinkText() {
    try {
      return getText(activeNavLink);
    } catch (Exception e) {
      return "";
    }
  }
}

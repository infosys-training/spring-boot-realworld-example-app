package io.spring.selenium.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class HomePage extends BasePage {

  @FindBy(css = ".navbar-brand")
  private WebElement navbarBrand;

  @FindBy(css = "a[href='/user/register']")
  private WebElement signUpLink;

  @FindBy(css = "a[href='/user/login']")
  private WebElement signInLink;

  @FindBy(css = "a[href='/user/settings']")
  private WebElement settingsLink;

  @FindBy(css = "a[href='/editor']")
  private WebElement newArticleLink;

  @FindBy(css = ".feed-toggle")
  private WebElement feedToggle;

  @FindBy(css = ".article-preview")
  private WebElement articlePreview;

  public HomePage(WebDriver driver) {
    super(driver);
  }

  public HomePage navigateTo(String baseUrl) {
    driver.get(baseUrl);
    return this;
  }

  public boolean isNavbarBrandDisplayed() {
    try {
      return waitForVisibility(navbarBrand).isDisplayed();
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

  public boolean isUserLoggedIn() {
    return isSettingsLinkDisplayed() || isNewArticleLinkDisplayed();
  }

  public boolean isUserLoggedOut() {
    return isSignUpLinkDisplayed() && isSignInLinkDisplayed();
  }

  public String getCurrentUrl() {
    return driver.getCurrentUrl();
  }

  public String getPageTitle() {
    return driver.getTitle();
  }
}

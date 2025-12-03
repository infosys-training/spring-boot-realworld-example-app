package io.spring.selenium.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

/** Page object for the Home page. */
public class HomePage extends BasePage {

  @FindBy(linkText = "Sign in")
  private WebElement signInLink;

  @FindBy(linkText = "Sign up")
  private WebElement signUpLink;

  @FindBy(linkText = "Settings")
  private WebElement settingsLink;

  @FindBy(css = ".nav-link[href*='profile']")
  private WebElement profileLink;

  @FindBy(css = ".navbar-brand")
  private WebElement homeLink;

  @FindBy(css = "a[href='/editor']")
  private WebElement newArticleLink;

  public HomePage(WebDriver driver) {
    super(driver);
  }

  public void navigateTo(String baseUrl) {
    driver.get(baseUrl);
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

  public void clickProfile() {
    click(profileLink);
  }

  public void clickHome() {
    click(homeLink);
  }

  public boolean isUserLoggedIn() {
    try {
      return isDisplayed(settingsLink);
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

  public void waitForPageLoad() {
    wait.until(ExpectedConditions.visibilityOf(homeLink));
  }

  public String getLoggedInUsername() {
    try {
      return getText(profileLink);
    } catch (Exception e) {
      return "";
    }
  }
}

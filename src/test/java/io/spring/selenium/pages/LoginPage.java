package io.spring.selenium.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/** Page Object for the Login page. */
public class LoginPage extends BasePage {

  private static final String LOGIN_URL = "/login";

  @FindBy(css = "input[placeholder='Email']")
  private WebElement emailInput;

  @FindBy(css = "input[placeholder='Password']")
  private WebElement passwordInput;

  @FindBy(css = "button[type='submit']")
  private WebElement signInButton;

  @FindBy(css = "h1")
  private WebElement pageTitle;

  @FindBy(css = "a[href='/register']")
  private WebElement registerLink;

  public LoginPage(WebDriver driver) {
    super(driver);
  }

  public LoginPage navigateTo(String baseUrl) {
    driver.get(baseUrl + LOGIN_URL);
    waitForVisibility(emailInput);
    return this;
  }

  public boolean isOnLoginPage() {
    return driver.getCurrentUrl().contains(LOGIN_URL);
  }

  public String getPageTitle() {
    return getText(pageTitle);
  }

  public boolean isEmailFieldDisplayed() {
    return isDisplayed(emailInput);
  }

  public boolean isPasswordFieldDisplayed() {
    return isDisplayed(passwordInput);
  }

  public boolean isSignInButtonDisplayed() {
    return isDisplayed(signInButton);
  }

  public boolean isRegisterLinkDisplayed() {
    return isDisplayed(registerLink);
  }
}

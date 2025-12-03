package io.spring.selenium.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class LoginPage extends BasePage {

  @FindBy(css = "input[type='email']")
  private WebElement emailInput;

  @FindBy(css = "input[type='password']")
  private WebElement passwordInput;

  @FindBy(css = "button[type='submit']")
  private WebElement signInButton;

  @FindBy(css = ".error-messages")
  private WebElement errorMessages;

  @FindBy(css = ".auth-page h1")
  private WebElement pageHeader;

  private static final String BASE_URL = "http://localhost:3000";
  private static final String LOGIN_URL = BASE_URL + "/user/login";

  public LoginPage(WebDriver driver) {
    super(driver);
  }

  public LoginPage navigateToLoginPage() {
    driver.get(LOGIN_URL);
    return this;
  }

  public boolean isLoginPageDisplayed() {
    try {
      return wait.until(ExpectedConditions.visibilityOf(pageHeader)).isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public LoginPage enterEmail(String email) {
    type(emailInput, email);
    return this;
  }

  public LoginPage enterPassword(String password) {
    type(passwordInput, password);
    return this;
  }

  public void clickSignIn() {
    click(signInButton);
  }

  public void login(String email, String password) {
    navigateToLoginPage();
    waitForVisibility(emailInput);
    enterEmail(email);
    enterPassword(password);
    clickSignIn();
    waitForLoginToComplete();
  }

  public void waitForLoginToComplete() {
    try {
      wait.until(ExpectedConditions.urlToBe(BASE_URL + "/"));
    } catch (Exception e) {
      // Login might have failed, check for error messages
    }
  }

  public boolean isLoggedIn() {
    try {
      return driver.getCurrentUrl().equals(BASE_URL + "/")
          || driver.getCurrentUrl().equals(BASE_URL);
    } catch (Exception e) {
      return false;
    }
  }

  public boolean hasErrorMessages() {
    try {
      return isDisplayed(errorMessages);
    } catch (Exception e) {
      return false;
    }
  }

  public String getErrorMessage() {
    try {
      return getText(errorMessages);
    } catch (Exception e) {
      return "";
    }
  }
}

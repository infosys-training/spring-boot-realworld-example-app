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

  @FindBy(css = ".auth-page h1")
  private WebElement pageTitle;

  @FindBy(css = ".error-messages")
  private WebElement errorMessages;

  private static final String BASE_URL = "http://localhost:3000";
  private static final String LOGIN_URL = BASE_URL + "/user/login";

  public LoginPage(WebDriver driver) {
    super(driver);
  }

  public void navigateToLoginPage() {
    driver.get(LOGIN_URL);
    waitForPageLoad();
  }

  public void waitForPageLoad() {
    wait.until(ExpectedConditions.visibilityOf(emailInput));
  }

  public void enterEmail(String email) {
    type(emailInput, email);
  }

  public void enterPassword(String password) {
    type(passwordInput, password);
  }

  public void clickSignIn() {
    click(signInButton);
  }

  public void login(String email, String password) {
    navigateToLoginPage();
    enterEmail(email);
    enterPassword(password);
    clickSignIn();
    waitForLoginComplete();
  }

  public void waitForLoginComplete() {
    try {
      wait.until(ExpectedConditions.urlToBe(BASE_URL + "/"));
    } catch (Exception e) {
      // Login might have failed or redirected elsewhere
    }
  }

  public boolean isLoginPageDisplayed() {
    try {
      return pageTitle.isDisplayed() && pageTitle.getText().contains("Sign in");
    } catch (Exception e) {
      return false;
    }
  }

  public boolean isErrorDisplayed() {
    try {
      return errorMessages.isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public String getErrorText() {
    try {
      return waitForVisibility(errorMessages).getText();
    } catch (Exception e) {
      return "";
    }
  }

  public String getCurrentUrl() {
    return driver.getCurrentUrl();
  }

  public boolean isLoggedIn() {
    return driver.getCurrentUrl().equals(BASE_URL + "/")
        || !driver.getCurrentUrl().contains("/login");
  }
}

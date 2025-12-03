package io.spring.selenium.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

/** Page Object for the Login page. */
public class LoginPage extends BasePage {

  private static final String LOGIN_URL = "http://localhost:3000/user/login";

  @FindBy(css = "input[type='email']")
  private WebElement emailInput;

  @FindBy(css = "input[type='password']")
  private WebElement passwordInput;

  @FindBy(css = "button[type='submit']")
  private WebElement signInButton;

  @FindBy(css = ".error-messages")
  private WebElement errorMessages;

  @FindBy(css = "h1.text-xs-center")
  private WebElement pageTitle;

  public LoginPage(WebDriver driver) {
    super(driver);
  }

  public void navigateTo() {
    driver.get(LOGIN_URL);
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
    navigateTo();
    enterEmail(email);
    enterPassword(password);
    clickSignIn();
  }

  public boolean isOnLoginPage() {
    try {
      return pageTitle.isDisplayed() && pageTitle.getText().contains("Sign in");
    } catch (Exception e) {
      return false;
    }
  }

  public boolean hasErrors() {
    try {
      return errorMessages.isDisplayed();
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

  public boolean isSignInButtonEnabled() {
    return signInButton.isEnabled();
  }
}

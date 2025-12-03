package io.spring.selenium.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class LoginPage extends BasePage {

  @FindBy(css = "input[placeholder='Email']")
  private WebElement emailInput;

  @FindBy(css = "input[placeholder='Password']")
  private WebElement passwordInput;

  @FindBy(css = "button[type='submit']")
  private WebElement signInButton;

  @FindBy(css = ".error-messages")
  private WebElement errorMessages;

  @FindBy(linkText = "Sign in")
  private WebElement signInLink;

  @FindBy(linkText = "Need an account?")
  private WebElement needAccountLink;

  public LoginPage(WebDriver driver) {
    super(driver);
  }

  public void navigateToLoginPage(String baseUrl) {
    driver.get(baseUrl + "/login");
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
    enterEmail(email);
    enterPassword(password);
    clickSignIn();
  }

  public boolean isErrorMessageDisplayed() {
    try {
      return waitForVisibility(errorMessages).isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public String getErrorMessage() {
    return getText(errorMessages);
  }

  public boolean isLoginPageDisplayed() {
    try {
      return emailInput.isDisplayed() && passwordInput.isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public boolean isSignInButtonEnabled() {
    return signInButton.isEnabled();
  }
}

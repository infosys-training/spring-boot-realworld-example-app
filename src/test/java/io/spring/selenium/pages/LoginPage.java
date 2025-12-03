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

  @FindBy(css = ".error-messages li")
  private WebElement errorMessageItem;

  public LoginPage(WebDriver driver) {
    super(driver);
  }

  public void navigateTo() {
    driver.get(getBaseUrl() + "/login");
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
    try {
      return getText(errorMessageItem);
    } catch (Exception e) {
      return "";
    }
  }

  public boolean isEmailInputDisplayed() {
    return isDisplayed(emailInput);
  }

  public boolean isPasswordInputDisplayed() {
    return isDisplayed(passwordInput);
  }

  public boolean isSignInButtonDisplayed() {
    return isDisplayed(signInButton);
  }

  public String getEmailValue() {
    return emailInput.getAttribute("value");
  }

  public String getPasswordValue() {
    return passwordInput.getAttribute("value");
  }

  public void clearEmail() {
    emailInput.clear();
  }

  public void clearPassword() {
    passwordInput.clear();
  }

  private String getBaseUrl() {
    return System.getProperty("base.url", "http://localhost:3000");
  }
}

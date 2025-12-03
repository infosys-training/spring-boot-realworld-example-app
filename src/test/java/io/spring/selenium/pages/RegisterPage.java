package io.spring.selenium.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class RegisterPage extends BasePage {

  @FindBy(css = "input[placeholder='Username']")
  private WebElement usernameInput;

  @FindBy(css = "input[placeholder='Email']")
  private WebElement emailInput;

  @FindBy(css = "input[placeholder='Password']")
  private WebElement passwordInput;

  @FindBy(css = "button[type='submit']")
  private WebElement signUpButton;

  @FindBy(css = ".error-messages")
  private WebElement errorMessages;

  @FindBy(css = ".error-messages li")
  private WebElement errorMessageItem;

  @FindBy(css = "a[href='/login']")
  private WebElement signInLink;

  public RegisterPage(WebDriver driver) {
    super(driver);
  }

  public void navigateTo() {
    driver.get(getBaseUrl() + "/register");
  }

  public void enterUsername(String username) {
    type(usernameInput, username);
  }

  public void enterEmail(String email) {
    type(emailInput, email);
  }

  public void enterPassword(String password) {
    type(passwordInput, password);
  }

  public void clickSignUp() {
    click(signUpButton);
  }

  public void register(String username, String email, String password) {
    enterUsername(username);
    enterEmail(email);
    enterPassword(password);
    clickSignUp();
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

  public void clickSignInLink() {
    click(signInLink);
  }

  public boolean isUsernameInputDisplayed() {
    return isDisplayed(usernameInput);
  }

  public boolean isEmailInputDisplayed() {
    return isDisplayed(emailInput);
  }

  public boolean isPasswordInputDisplayed() {
    return isDisplayed(passwordInput);
  }

  public boolean isSignUpButtonDisplayed() {
    return isDisplayed(signUpButton);
  }

  public boolean isPageLoaded() {
    return isUsernameInputDisplayed() && isEmailInputDisplayed() && isPasswordInputDisplayed();
  }

  private String getBaseUrl() {
    return System.getProperty("base.url", "http://localhost:3000");
  }
}

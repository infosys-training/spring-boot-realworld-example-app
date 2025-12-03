package io.spring.selenium.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class RegistrationPage extends BasePage {

  private static final String REGISTRATION_URL = "/user/register";

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

  @FindBy(css = "h1")
  private WebElement pageTitle;

  @FindBy(css = "a[href='/user/login']")
  private WebElement loginLink;

  public RegistrationPage(WebDriver driver) {
    super(driver);
  }

  public RegistrationPage navigateTo(String baseUrl) {
    driver.get(baseUrl + REGISTRATION_URL);
    return this;
  }

  public RegistrationPage enterUsername(String username) {
    type(usernameInput, username);
    return this;
  }

  public RegistrationPage enterEmail(String email) {
    type(emailInput, email);
    return this;
  }

  public RegistrationPage enterPassword(String password) {
    type(passwordInput, password);
    return this;
  }

  public RegistrationPage clickSignUp() {
    click(signUpButton);
    return this;
  }

  public RegistrationPage register(String username, String email, String password) {
    enterUsername(username);
    enterEmail(email);
    enterPassword(password);
    clickSignUp();
    return this;
  }

  public boolean isErrorMessageDisplayed() {
    try {
      return waitForVisibility(errorMessages).isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public String getErrorMessageText() {
    try {
      return getText(errorMessages);
    } catch (Exception e) {
      return "";
    }
  }

  public boolean isPageTitleDisplayed() {
    try {
      return waitForVisibility(pageTitle).isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public String getPageTitleText() {
    return getText(pageTitle);
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

  public boolean isSignUpButtonEnabled() {
    try {
      return signUpButton.isEnabled();
    } catch (Exception e) {
      return false;
    }
  }

  public String getUsernameValue() {
    return usernameInput.getAttribute("value");
  }

  public String getEmailValue() {
    return emailInput.getAttribute("value");
  }

  public String getPasswordValue() {
    return passwordInput.getAttribute("value");
  }

  public RegistrationPage clearUsername() {
    usernameInput.clear();
    return this;
  }

  public RegistrationPage clearEmail() {
    emailInput.clear();
    return this;
  }

  public RegistrationPage clearPassword() {
    passwordInput.clear();
    return this;
  }

  public void clickLoginLink() {
    click(loginLink);
  }

  public void tabThroughFields() {
    usernameInput.sendKeys("\t");
  }

  public WebElement getUsernameInput() {
    return usernameInput;
  }

  public WebElement getEmailInput() {
    return emailInput;
  }

  public WebElement getPasswordInput() {
    return passwordInput;
  }

  public WebElement getSignUpButton() {
    return signUpButton;
  }
}

package io.spring.selenium.pages;

import java.util.List;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class RegistrationPage extends BasePage {

  @FindBy(css = "input[placeholder='Username']")
  private WebElement usernameInput;

  @FindBy(css = "input[placeholder='Email']")
  private WebElement emailInput;

  @FindBy(css = "input[placeholder='Password']")
  private WebElement passwordInput;

  @FindBy(css = "button[type='submit']")
  private WebElement signUpButton;

  @FindBy(css = ".error-messages li")
  private List<WebElement> errorMessages;

  @FindBy(css = ".error-messages")
  private WebElement errorMessagesContainer;

  @FindBy(css = "a[href='/register']")
  private WebElement signUpLink;

  private static final String REGISTRATION_URL = "/register";

  public RegistrationPage(WebDriver driver) {
    super(driver);
  }

  public RegistrationPage navigateTo(String baseUrl) {
    driver.get(baseUrl + REGISTRATION_URL);
    waitForPageLoad();
    return this;
  }

  public void waitForPageLoad() {
    wait.until(ExpectedConditions.visibilityOf(usernameInput));
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

  public void clickSignUp() {
    click(signUpButton);
  }

  public void register(String username, String email, String password) {
    enterUsername(username);
    enterEmail(email);
    enterPassword(password);
    clickSignUp();
  }

  public boolean isUsernameFieldDisplayed() {
    return isDisplayed(usernameInput);
  }

  public boolean isEmailFieldDisplayed() {
    return isDisplayed(emailInput);
  }

  public boolean isPasswordFieldDisplayed() {
    return isDisplayed(passwordInput);
  }

  public boolean isSignUpButtonDisplayed() {
    return isDisplayed(signUpButton);
  }

  public List<String> getErrorMessages() {
    try {
      wait.until(ExpectedConditions.visibilityOf(errorMessagesContainer));
      return errorMessages.stream().map(WebElement::getText).toList();
    } catch (Exception e) {
      return List.of();
    }
  }

  public boolean hasErrorMessages() {
    try {
      wait.until(ExpectedConditions.visibilityOf(errorMessagesContainer));
      return !errorMessages.isEmpty();
    } catch (Exception e) {
      return false;
    }
  }

  public String getErrorMessagesText() {
    try {
      wait.until(ExpectedConditions.visibilityOf(errorMessagesContainer));
      return getText(errorMessagesContainer);
    } catch (Exception e) {
      return "";
    }
  }

  public boolean isErrorMessageDisplayed(String expectedMessage) {
    List<String> errors = getErrorMessages();
    return errors.stream()
        .anyMatch(error -> error.toLowerCase().contains(expectedMessage.toLowerCase()));
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

  public String getPasswordInputType() {
    return passwordInput.getAttribute("type");
  }

  public boolean isSignUpButtonEnabled() {
    return signUpButton.isEnabled();
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

  public RegistrationPage clearAllFields() {
    clearUsername();
    clearEmail();
    clearPassword();
    return this;
  }

  public String getCurrentUrl() {
    return driver.getCurrentUrl();
  }

  public boolean isOnRegistrationPage() {
    return driver.getCurrentUrl().contains(REGISTRATION_URL);
  }
}

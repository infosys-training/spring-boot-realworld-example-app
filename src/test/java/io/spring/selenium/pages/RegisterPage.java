package io.spring.selenium.pages;

import java.util.List;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/** Page Object for the Registration page. */
public class RegisterPage extends BasePage {

  private static final String REGISTER_URL = "/register";

  @FindBy(css = "input[placeholder='Username']")
  private WebElement usernameInput;

  @FindBy(css = "input[placeholder='Email']")
  private WebElement emailInput;

  @FindBy(css = "input[placeholder='Password']")
  private WebElement passwordInput;

  @FindBy(css = "button[type='submit']")
  private WebElement signUpButton;

  @FindBy(css = "h1")
  private WebElement pageTitle;

  @FindBy(css = "a[href='/login']")
  private WebElement loginLink;

  @FindBy(css = ".error-messages li")
  private List<WebElement> errorMessages;

  @FindBy(css = ".error-messages")
  private WebElement errorContainer;

  public RegisterPage(WebDriver driver) {
    super(driver);
  }

  public RegisterPage navigateTo(String baseUrl) {
    driver.get(baseUrl + REGISTER_URL);
    waitForVisibility(usernameInput);
    return this;
  }

  public RegisterPage enterUsername(String username) {
    type(usernameInput, username);
    return this;
  }

  public RegisterPage enterEmail(String email) {
    type(emailInput, email);
    return this;
  }

  public RegisterPage enterPassword(String password) {
    type(passwordInput, password);
    return this;
  }

  public RegisterPage clickSignUp() {
    click(signUpButton);
    return this;
  }

  public void clickLoginLink() {
    click(loginLink);
  }

  public String getPageTitle() {
    return getText(pageTitle);
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

  public boolean isSignUpButtonEnabled() {
    try {
      return signUpButton.isEnabled();
    } catch (Exception e) {
      return false;
    }
  }

  public boolean isLoginLinkDisplayed() {
    return isDisplayed(loginLink);
  }

  public String getUsernamePlaceholder() {
    return usernameInput.getAttribute("placeholder");
  }

  public String getEmailPlaceholder() {
    return emailInput.getAttribute("placeholder");
  }

  public String getPasswordPlaceholder() {
    return passwordInput.getAttribute("placeholder");
  }

  public String getPasswordFieldType() {
    return passwordInput.getAttribute("type");
  }

  public String getEmailFieldType() {
    return emailInput.getAttribute("type");
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

  public List<String> getErrorMessages() {
    try {
      waitForVisibility(errorContainer);
      return errorMessages.stream().map(WebElement::getText).toList();
    } catch (Exception e) {
      return List.of();
    }
  }

  public boolean hasErrors() {
    try {
      return isDisplayed(errorContainer) && !errorMessages.isEmpty();
    } catch (Exception e) {
      return false;
    }
  }

  public boolean isOnRegisterPage() {
    return driver.getCurrentUrl().contains(REGISTER_URL);
  }

  public RegisterPage register(String username, String email, String password) {
    enterUsername(username);
    enterEmail(email);
    enterPassword(password);
    clickSignUp();
    return this;
  }

  public void clearUsername() {
    usernameInput.clear();
  }

  public void clearEmail() {
    emailInput.clear();
  }

  public void clearPassword() {
    passwordInput.clear();
  }
}

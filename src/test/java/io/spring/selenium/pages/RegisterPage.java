package io.spring.selenium.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class RegisterPage extends BasePage {

  private static final String REGISTER_URL = "/user/register";

  @FindBy(css = "input[placeholder='Username']")
  private WebElement usernameInput;

  @FindBy(css = "input[placeholder='Email']")
  private WebElement emailInput;

  @FindBy(css = "input[placeholder='Password']")
  private WebElement passwordInput;

  @FindBy(css = "button[type='submit']")
  private WebElement signUpButton;

  @FindBy(css = "h1.text-xs-center")
  private WebElement pageTitle;

  @FindBy(css = "a[href='/user/login']")
  private WebElement loginLink;

  @FindBy(css = "ul.error-messages")
  private WebElement errorMessages;

  @FindBy(css = "ul.error-messages li")
  private WebElement errorMessageItem;

  public RegisterPage(WebDriver driver) {
    super(driver);
  }

  public RegisterPage navigateTo(String baseUrl) {
    driver.get(baseUrl + REGISTER_URL);
    waitForPageLoad();
    return this;
  }

  public void waitForPageLoad() {
    waitForVisibility(pageTitle);
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

  public void clickLoginLink() {
    click(loginLink);
  }

  public boolean isErrorMessageDisplayed() {
    try {
      waitForVisibility(errorMessages);
      return isDisplayed(errorMessages);
    } catch (Exception e) {
      return false;
    }
  }

  public String getErrorMessage() {
    try {
      waitForVisibility(errorMessageItem);
      return getText(errorMessageItem);
    } catch (Exception e) {
      return "";
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

  public String getUsernamePlaceholder() {
    return usernameInput.getAttribute("placeholder");
  }

  public String getEmailPlaceholder() {
    return emailInput.getAttribute("placeholder");
  }

  public String getPasswordPlaceholder() {
    return passwordInput.getAttribute("placeholder");
  }

  public String getPasswordInputType() {
    return passwordInput.getAttribute("type");
  }

  public String getEmailInputType() {
    return emailInput.getAttribute("type");
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

  public void clearAllFields() {
    clearUsername();
    clearEmail();
    clearPassword();
  }

  public String getCurrentUrl() {
    return driver.getCurrentUrl();
  }

  public String getPageSource() {
    return driver.getPageSource();
  }
}

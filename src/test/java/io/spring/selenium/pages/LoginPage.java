package io.spring.selenium.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class LoginPage extends BasePage {

  private static final String LOGIN_URL = "/user/login";

  @FindBy(css = "input[type='email']")
  private WebElement emailInput;

  @FindBy(css = "input[type='password']")
  private WebElement passwordInput;

  @FindBy(css = "button[type='submit']")
  private WebElement signInButton;

  @FindBy(css = ".error-messages")
  private WebElement errorMessages;

  @FindBy(css = ".error-messages li")
  private WebElement errorMessageItem;

  @FindBy(css = "h1.text-xs-center")
  private WebElement pageTitle;

  @FindBy(linkText = "Need an account?")
  private WebElement needAccountLink;

  public LoginPage(WebDriver driver) {
    super(driver);
  }

  public LoginPage navigateTo(String baseUrl) {
    driver.get(baseUrl + LOGIN_URL);
    waitForPageLoad();
    return this;
  }

  public void waitForPageLoad() {
    wait.until(ExpectedConditions.visibilityOf(emailInput));
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

  public HomePage login(String email, String password) {
    enterEmail(email);
    enterPassword(password);
    clickSignIn();
    return new HomePage(driver);
  }

  public LoginPage loginExpectingError(String email, String password) {
    enterEmail(email);
    enterPassword(password);
    clickSignIn();
    waitForErrorMessage();
    return this;
  }

  public void waitForErrorMessage() {
    wait.until(ExpectedConditions.visibilityOf(errorMessages));
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
      waitForErrorMessage();
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

  public boolean isSignInButtonEnabled() {
    try {
      return signInButton.isEnabled();
    } catch (Exception e) {
      return false;
    }
  }

  public String getPageTitle() {
    return getText(pageTitle);
  }

  public String getEmailInputType() {
    return emailInput.getAttribute("type");
  }

  public String getPasswordInputType() {
    return passwordInput.getAttribute("type");
  }

  public String getEmailPlaceholder() {
    return emailInput.getAttribute("placeholder");
  }

  public String getPasswordPlaceholder() {
    return passwordInput.getAttribute("placeholder");
  }

  public String getEmailValue() {
    return emailInput.getAttribute("value");
  }

  public String getPasswordValue() {
    return passwordInput.getAttribute("value");
  }

  public void clearEmailField() {
    emailInput.clear();
  }

  public void clearPasswordField() {
    passwordInput.clear();
  }

  public void clearAllFields() {
    clearEmailField();
    clearPasswordField();
  }

  public boolean isNeedAccountLinkDisplayed() {
    return isDisplayed(needAccountLink);
  }

  public void clickNeedAccountLink() {
    click(needAccountLink);
  }

  public String getCurrentUrl() {
    return driver.getCurrentUrl();
  }

  public String getPageSource() {
    return driver.getPageSource();
  }
}

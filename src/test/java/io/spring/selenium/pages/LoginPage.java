package io.spring.selenium.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/** Page Object for the Login page. */
public class LoginPage extends BasePage {

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

  @FindBy(linkText = "Need an account?")
  private WebElement registerLink;

  public LoginPage(WebDriver driver) {
    super(driver);
  }

  public void navigateTo() {
    driver.get(getBaseUrl() + "/user/login");
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

  public boolean isErrorDisplayed() {
    try {
      return waitForVisibility(errorMessages).isDisplayed();
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

  public boolean isPageLoaded() {
    try {
      return waitForVisibility(pageTitle).getText().contains("Sign in");
    } catch (Exception e) {
      return false;
    }
  }

  public boolean isSignInButtonEnabled() {
    return signInButton.isEnabled();
  }

  public void clickRegisterLink() {
    click(registerLink);
  }

  public String getEmailInputValue() {
    return emailInput.getAttribute("value");
  }

  public String getPasswordInputValue() {
    return passwordInput.getAttribute("value");
  }

  public String getPasswordInputType() {
    return passwordInput.getAttribute("type");
  }

  private String getBaseUrl() {
    return System.getProperty("base.url", "http://localhost:3000");
  }
}

package io.spring.selenium.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/** Page object for the login page. */
public class LoginPage extends BasePage {

  private static final String LOGIN_URL = "/user/login";

  @FindBy(css = "input[placeholder='Email']")
  private WebElement emailInput;

  @FindBy(css = "input[placeholder='Password']")
  private WebElement passwordInput;

  @FindBy(css = "button[type='submit']")
  private WebElement signInButton;

  @FindBy(css = ".error-messages")
  private WebElement errorMessages;

  @FindBy(css = "a[href='/user/register']")
  private WebElement registerLink;

  public LoginPage(WebDriver driver) {
    super(driver);
  }

  public LoginPage navigateTo(String baseUrl) {
    driver.get(baseUrl + LOGIN_URL);
    return this;
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

  public boolean isErrorDisplayed() {
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
      return isDisplayed(emailInput) && isDisplayed(passwordInput) && isDisplayed(signInButton);
    } catch (Exception e) {
      return false;
    }
  }

  public void clickRegisterLink() {
    click(registerLink);
  }
}

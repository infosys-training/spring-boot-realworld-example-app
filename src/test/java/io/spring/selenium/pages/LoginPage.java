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

  @FindBy(css = "a[href='/user/register']")
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

  public boolean isOnLoginPage() {
    try {
      return waitForVisibility(pageTitle).getText().contains("Sign in");
    } catch (Exception e) {
      return false;
    }
  }

  public boolean hasErrorMessages() {
    try {
      return isDisplayed(errorMessages);
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

  public void clickRegisterLink() {
    click(registerLink);
  }

  private String getBaseUrl() {
    return System.getProperty("base.url", "http://localhost:3000");
  }
}

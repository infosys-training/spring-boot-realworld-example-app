package io.spring.selenium.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

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

  @FindBy(css = "h1.text-xs-center")
  private WebElement pageTitle;

  public LoginPage(WebDriver driver) {
    super(driver);
  }

  public LoginPage navigateTo(String baseUrl) {
    driver.get(baseUrl + LOGIN_URL);
    return this;
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

  public boolean hasErrors() {
    try {
      return isDisplayed(errorMessages);
    } catch (Exception e) {
      return false;
    }
  }
}

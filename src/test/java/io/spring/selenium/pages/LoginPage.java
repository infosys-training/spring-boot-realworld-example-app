package io.spring.selenium.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/** Page object for the Login page. */
public class LoginPage extends BasePage {

  @FindBy(css = "input[placeholder='Email']")
  private WebElement emailInput;

  @FindBy(css = "input[placeholder='Password']")
  private WebElement passwordInput;

  @FindBy(css = "button[type='submit']")
  private WebElement signInButton;

  @FindBy(css = ".error-messages")
  private WebElement errorMessages;

  @FindBy(css = "a[href='/register']")
  private WebElement signUpLink;

  public LoginPage(WebDriver driver) {
    super(driver);
  }

  public void navigateTo() {
    driver.get(System.getProperty("base.url", "http://localhost:3000") + "/login");
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

  public boolean isLoginPageDisplayed() {
    try {
      return isDisplayed(emailInput) && isDisplayed(passwordInput);
    } catch (Exception e) {
      return false;
    }
  }

  public void clickSignUpLink() {
    click(signUpLink);
  }
}

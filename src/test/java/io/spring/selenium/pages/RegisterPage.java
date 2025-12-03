package io.spring.selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class RegisterPage extends BasePage {

  private static final String REGISTER_URL = "http://localhost:3000/register";

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

  @FindBy(css = "h1")
  private WebElement pageTitle;

  public RegisterPage(WebDriver driver) {
    super(driver);
  }

  public RegisterPage navigate() {
    driver.get(REGISTER_URL);
    waitForPageLoad();
    return this;
  }

  public void waitForPageLoad() {
    wait.until(
        ExpectedConditions.visibilityOfElementLocated(
            By.cssSelector("input[placeholder='Username']")));
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

  public HomePage clickSignUp() {
    click(signUpButton);
    try {
      wait.until(ExpectedConditions.urlContains("localhost:3000"));
      wait.until(
          ExpectedConditions.invisibilityOfElementLocated(
              By.cssSelector("input[placeholder='Username']")));
    } catch (Exception e) {
      // Registration might have failed
    }
    return new HomePage(driver);
  }

  public HomePage register(String username, String email, String password) {
    enterUsername(username);
    enterEmail(email);
    enterPassword(password);
    return clickSignUp();
  }

  public boolean hasErrorMessages() {
    try {
      return errorMessages.isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public String getErrorMessages() {
    try {
      return getText(errorMessages);
    } catch (Exception e) {
      return "";
    }
  }

  public String getPageTitle() {
    try {
      return getText(pageTitle);
    } catch (Exception e) {
      return "";
    }
  }

  public boolean isOnRegisterPage() {
    return driver.getCurrentUrl().contains("/register");
  }
}

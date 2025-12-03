package io.spring.selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class LoginPage extends BasePage {

  private static final String LOGIN_URL = "http://localhost:3000/login";

  @FindBy(css = "input[placeholder='Email']")
  private WebElement emailInput;

  @FindBy(css = "input[placeholder='Password']")
  private WebElement passwordInput;

  @FindBy(css = "button[type='submit']")
  private WebElement signInButton;

  @FindBy(css = ".error-messages")
  private WebElement errorMessages;

  @FindBy(css = "h1")
  private WebElement pageTitle;

  public LoginPage(WebDriver driver) {
    super(driver);
  }

  public LoginPage navigate() {
    driver.get(LOGIN_URL);
    waitForPageLoad();
    return this;
  }

  public void waitForPageLoad() {
    wait.until(
        ExpectedConditions.visibilityOfElementLocated(
            By.cssSelector("input[placeholder='Email']")));
  }

  public LoginPage enterEmail(String email) {
    type(emailInput, email);
    return this;
  }

  public LoginPage enterPassword(String password) {
    type(passwordInput, password);
    return this;
  }

  public HomePage clickSignIn() {
    click(signInButton);
    try {
      wait.until(ExpectedConditions.urlContains("localhost:3000"));
      wait.until(
          ExpectedConditions.invisibilityOfElementLocated(
              By.cssSelector("input[placeholder='Email']")));
    } catch (Exception e) {
      // Login might have failed
    }
    return new HomePage(driver);
  }

  public LoginPage clickSignInExpectingError() {
    click(signInButton);
    return this;
  }

  public HomePage login(String email, String password) {
    enterEmail(email);
    enterPassword(password);
    return clickSignIn();
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

  public boolean isOnLoginPage() {
    return driver.getCurrentUrl().contains("/login");
  }
}

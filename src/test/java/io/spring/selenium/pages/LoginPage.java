package io.spring.selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

/** Page Object for the Login page. */
public class LoginPage extends BasePage {

  @FindBy(css = "input[placeholder='Email']")
  private WebElement emailInput;

  @FindBy(css = "input[placeholder='Password']")
  private WebElement passwordInput;

  @FindBy(css = "button[type='submit']")
  private WebElement signInButton;

  @FindBy(css = ".error-messages")
  private WebElement errorMessages;

  @FindBy(css = "a[href='/user/login']")
  private WebElement signInLink;

  private static final String BASE_URL = "http://localhost:3000";
  private static final String LOGIN_URL = BASE_URL + "/user/login";

  public LoginPage(WebDriver driver) {
    super(driver);
  }

  public void navigateToLoginPage() {
    driver.get(LOGIN_URL);
    waitForPageLoad();
  }

  public void waitForPageLoad() {
    try {
      wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input")));
    } catch (Exception e) {
      // Page may have loaded with different structure
    }
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
    navigateToLoginPage();
    enterEmail(email);
    enterPassword(password);
    clickSignIn();
    waitForLoginComplete();
  }

  public void waitForLoginComplete() {
    try {
      // Wait for redirect away from login page or for error message
      wait.until(
          ExpectedConditions.or(
              ExpectedConditions.urlContains("/"),
              ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".error-messages"))));
      Thread.sleep(1000); // Allow time for state to update
    } catch (Exception e) {
      // Login may have completed or failed
    }
  }

  public boolean isLoggedIn() {
    try {
      // Check if we're no longer on login page and user nav is visible
      String currentUrl = driver.getCurrentUrl();
      return !currentUrl.contains("/user/login")
          && driver.findElements(By.cssSelector("a[href='/user/login']")).isEmpty();
    } catch (Exception e) {
      return false;
    }
  }

  public boolean hasErrorMessage() {
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
}

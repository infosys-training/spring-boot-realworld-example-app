package io.spring.selenium.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class LoginPage extends BasePage {

  @FindBy(css = "input[placeholder='Email']")
  private WebElement emailInput;

  @FindBy(css = "input[placeholder='Password']")
  private WebElement passwordInput;

  @FindBy(css = "button[type='submit']")
  private WebElement signInButton;

  @FindBy(css = ".error-messages li")
  private WebElement errorMessage;

  @FindBy(css = "h1.text-xs-center")
  private WebElement pageTitle;

  @FindBy(css = "a[href='/user/register']")
  private WebElement needAccountLink;

  public LoginPage(WebDriver driver) {
    super(driver);
  }

  public void navigateTo(String baseUrl) {
    driver.get(baseUrl + "/user/login");
    waitForPageLoad();
  }

  public void waitForPageLoad() {
    wait.until(ExpectedConditions.visibilityOf(emailInput));
  }

  public boolean isLoginPageDisplayed() {
    try {
      return emailInput.isDisplayed() && passwordInput.isDisplayed();
    } catch (Exception e) {
      return false;
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
    enterEmail(email);
    enterPassword(password);
    clickSignIn();
  }

  public String getErrorMessage() {
    try {
      waitForVisibility(errorMessage);
      return errorMessage.getText();
    } catch (Exception e) {
      return null;
    }
  }

  public boolean isErrorDisplayed() {
    try {
      return errorMessage.isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public String getPageTitle() {
    return getText(pageTitle);
  }

  public void clickNeedAccountLink() {
    click(needAccountLink);
  }

  public void waitForLoginSuccess() {
    wait.until(ExpectedConditions.invisibilityOf(signInButton));
  }
}

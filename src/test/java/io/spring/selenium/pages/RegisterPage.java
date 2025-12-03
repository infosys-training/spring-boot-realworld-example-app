package io.spring.selenium.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/** Page object for the Registration page. */
public class RegisterPage extends BasePage {

  @FindBy(css = "h1.text-xs-center")
  private WebElement pageTitle;

  @FindBy(css = "input[placeholder='Username']")
  private WebElement usernameInput;

  @FindBy(css = "input[placeholder='Email']")
  private WebElement emailInput;

  @FindBy(css = "input[placeholder='Password']")
  private WebElement passwordInput;

  @FindBy(css = "button[type='submit']")
  private WebElement signUpButton;

  @FindBy(css = ".error-messages li")
  private WebElement errorMessage;

  @FindBy(css = "a[href='/user/login']")
  private WebElement haveAccountLink;

  public RegisterPage(WebDriver driver) {
    super(driver);
  }

  public void navigateTo(String baseUrl) {
    driver.get(baseUrl + "/user/register");
  }

  public void enterUsername(String username) {
    type(usernameInput, username);
  }

  public void enterEmail(String email) {
    type(emailInput, email);
  }

  public void enterPassword(String password) {
    type(passwordInput, password);
  }

  public void clickSignUp() {
    click(signUpButton);
  }

  public void register(String username, String email, String password) {
    enterUsername(username);
    enterEmail(email);
    enterPassword(password);
    clickSignUp();
  }

  public String getPageTitleText() {
    return getText(pageTitle);
  }

  public boolean isErrorMessageDisplayed() {
    try {
      return waitForVisibility(errorMessage).isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public String getErrorMessage() {
    try {
      return getText(errorMessage);
    } catch (Exception e) {
      return null;
    }
  }

  public void clickHaveAccountLink() {
    click(haveAccountLink);
  }

  public boolean isUsernameInputDisplayed() {
    return isDisplayed(usernameInput);
  }

  public boolean isEmailInputDisplayed() {
    return isDisplayed(emailInput);
  }

  public boolean isPasswordInputDisplayed() {
    return isDisplayed(passwordInput);
  }

  public boolean isSignUpButtonDisplayed() {
    return isDisplayed(signUpButton);
  }

  public boolean isSignUpButtonEnabled() {
    return signUpButton.isEnabled();
  }
}

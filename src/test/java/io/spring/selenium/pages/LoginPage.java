package io.spring.selenium.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/** Page Object for the Login page. */
public class LoginPage extends BasePage {

  private static final String LOGIN_URL = "/user/login";

  @FindBy(css = "input[placeholder='Email']")
  private WebElement emailInput;

  @FindBy(css = "input[placeholder='Password']")
  private WebElement passwordInput;

  @FindBy(css = "button[type='submit']")
  private WebElement signInButton;

  @FindBy(css = "h1.text-xs-center")
  private WebElement pageTitle;

  @FindBy(css = "a[href='/user/register']")
  private WebElement registerLink;

  public LoginPage(WebDriver driver) {
    super(driver);
  }

  public LoginPage navigateTo(String baseUrl) {
    driver.get(baseUrl + LOGIN_URL);
    waitForPageLoad();
    return this;
  }

  public void waitForPageLoad() {
    waitForVisibility(emailInput);
  }

  public boolean isOnLoginPage() {
    return driver.getCurrentUrl().contains(LOGIN_URL);
  }

  public String getPageTitle() {
    return getText(pageTitle);
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
}

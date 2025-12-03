package io.spring.selenium.pages;

import java.util.List;
import java.util.stream.Collectors;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class LoginPage extends BasePage {

  private static final String LOGIN_URL = "/user/login";

  @FindBy(css = "input[placeholder='Email']")
  private WebElement emailInput;

  @FindBy(css = "input[placeholder='Password']")
  private WebElement passwordInput;

  @FindBy(css = "button[type='submit']")
  private WebElement signInButton;

  @FindBy(css = "ul.error-messages li")
  private List<WebElement> errorMessages;

  @FindBy(css = "ul.error-messages")
  private WebElement errorMessagesContainer;

  @FindBy(css = "h1.text-xs-center")
  private WebElement pageTitle;

  public LoginPage(WebDriver driver) {
    super(driver);
  }

  public LoginPage navigateTo(String baseUrl) {
    driver.get(baseUrl + LOGIN_URL);
    waitForVisibility(pageTitle);
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

  public LoginPage clickSignIn() {
    click(signInButton);
    return this;
  }

  public LoginPage clearEmail() {
    waitForVisibility(emailInput).clear();
    return this;
  }

  public LoginPage clearPassword() {
    waitForVisibility(passwordInput).clear();
    return this;
  }

  public boolean isErrorDisplayed() {
    try {
      return waitForVisibility(errorMessagesContainer).isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public List<String> getErrorMessages() {
    waitForVisibility(errorMessagesContainer);
    return errorMessages.stream().map(WebElement::getText).collect(Collectors.toList());
  }

  public int getErrorCount() {
    try {
      waitForVisibility(errorMessagesContainer);
      return errorMessages.size();
    } catch (Exception e) {
      return 0;
    }
  }

  public boolean hasErrorContaining(String text) {
    return getErrorMessages().stream()
        .anyMatch(msg -> msg.toLowerCase().contains(text.toLowerCase()));
  }

  public boolean isOnLoginPage() {
    try {
      return pageTitle.getText().contains("Sign in");
    } catch (Exception e) {
      return false;
    }
  }

  public String getPageTitle() {
    return getText(pageTitle);
  }
}

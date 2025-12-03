package io.spring.selenium.pages;

import java.util.List;
import java.util.stream.Collectors;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class RegisterPage extends BasePage {

  private static final String REGISTER_URL = "/user/register";

  @FindBy(css = "input[placeholder='Username']")
  private WebElement usernameInput;

  @FindBy(css = "input[placeholder='Email']")
  private WebElement emailInput;

  @FindBy(css = "input[placeholder='Password']")
  private WebElement passwordInput;

  @FindBy(css = "button[type='submit']")
  private WebElement signUpButton;

  @FindBy(css = "ul.error-messages li")
  private List<WebElement> errorMessages;

  @FindBy(css = "ul.error-messages")
  private WebElement errorMessagesContainer;

  @FindBy(css = "h1.text-xs-center")
  private WebElement pageTitle;

  public RegisterPage(WebDriver driver) {
    super(driver);
  }

  public RegisterPage navigateTo(String baseUrl) {
    driver.get(baseUrl + REGISTER_URL);
    waitForVisibility(pageTitle);
    return this;
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

  public RegisterPage clickSignUp() {
    click(signUpButton);
    return this;
  }

  public RegisterPage clearUsername() {
    waitForVisibility(usernameInput).clear();
    return this;
  }

  public RegisterPage clearEmail() {
    waitForVisibility(emailInput).clear();
    return this;
  }

  public RegisterPage clearPassword() {
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

  public boolean isOnRegisterPage() {
    try {
      return pageTitle.getText().contains("Sign Up");
    } catch (Exception e) {
      return false;
    }
  }

  public String getPageTitle() {
    return getText(pageTitle);
  }
}

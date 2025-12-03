package io.spring.selenium.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class SettingsPage extends BasePage {

  private static final String SETTINGS_URL = "/user/settings";

  @FindBy(css = ".settings-page")
  private WebElement settingsPageContainer;

  @FindBy(css = "h1.text-xs-center")
  private WebElement pageTitle;

  @FindBy(css = "button.btn-outline-danger")
  private WebElement logoutButton;

  @FindBy(css = "input[placeholder='URL of profile picture']")
  private WebElement imageUrlInput;

  @FindBy(css = "input[placeholder='Username']")
  private WebElement usernameInput;

  @FindBy(css = "textarea[placeholder='Short bio about you']")
  private WebElement bioTextarea;

  @FindBy(css = "input[placeholder='Email']")
  private WebElement emailInput;

  @FindBy(css = "input[placeholder='New Password']")
  private WebElement newPasswordInput;

  @FindBy(css = "button[type='submit']")
  private WebElement updateSettingsButton;

  public SettingsPage(WebDriver driver) {
    super(driver);
  }

  public SettingsPage navigateTo(String baseUrl) {
    driver.get(baseUrl + SETTINGS_URL);
    waitForPageLoad();
    return this;
  }

  public void waitForPageLoad() {
    wait.until(ExpectedConditions.visibilityOf(settingsPageContainer));
  }

  public boolean isSettingsPageDisplayed() {
    try {
      return isDisplayed(settingsPageContainer);
    } catch (Exception e) {
      return false;
    }
  }

  public String getPageTitle() {
    return getText(pageTitle);
  }

  public HomePage logout() {
    click(logoutButton);
    return new HomePage(driver);
  }

  public boolean isLogoutButtonDisplayed() {
    return isDisplayed(logoutButton);
  }

  public String getLogoutButtonText() {
    return getText(logoutButton);
  }

  public String getCurrentUrl() {
    return driver.getCurrentUrl();
  }

  public String getEmailValue() {
    try {
      return emailInput.getAttribute("value");
    } catch (Exception e) {
      return "";
    }
  }

  public String getUsernameValue() {
    try {
      return usernameInput.getAttribute("value");
    } catch (Exception e) {
      return "";
    }
  }
}

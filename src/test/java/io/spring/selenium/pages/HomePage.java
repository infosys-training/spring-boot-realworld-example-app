package io.spring.selenium.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class HomePage extends BasePage {

  @FindBy(css = "a.navbar-brand")
  private WebElement brandLink;

  @FindBy(css = "a[href='/user/login']")
  private WebElement signInLink;

  @FindBy(css = "a[href='/user/register']")
  private WebElement signUpLink;

  @FindBy(css = "a[href='/editor/new']")
  private WebElement newArticleLink;

  @FindBy(css = "a[href='/user/settings']")
  private WebElement settingsLink;

  @FindBy(css = ".home-page")
  private WebElement homeContainer;

  public HomePage(WebDriver driver) {
    super(driver);
  }

  public HomePage navigateTo(String baseUrl) {
    driver.get(baseUrl);
    waitForVisibility(homeContainer);
    return this;
  }

  public LoginPage clickSignIn() {
    click(signInLink);
    return new LoginPage(driver);
  }

  public RegisterPage clickSignUp() {
    click(signUpLink);
    return new RegisterPage(driver);
  }

  public ArticleEditorPage clickNewArticle() {
    click(newArticleLink);
    return new ArticleEditorPage(driver);
  }

  public SettingsPage clickSettings() {
    click(settingsLink);
    return new SettingsPage(driver);
  }

  public boolean isLoggedIn() {
    try {
      return newArticleLink.isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public boolean isOnHomePage() {
    try {
      return homeContainer.isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public void loginAs(String baseUrl, String email, String password) {
    navigateTo(baseUrl);
    LoginPage loginPage = clickSignIn();
    loginPage.enterEmail(email).enterPassword(password).clickSignIn();
    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }
}

package io.spring.selenium.pages;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class HomePage extends BasePage {

  @FindBy(css = "a[href='/']")
  private WebElement homeLink;

  @FindBy(css = "a[href='/editor']")
  private WebElement newArticleLink;

  @FindBy(css = "a[href='/settings']")
  private WebElement settingsLink;

  @FindBy(css = "a[href='/user/login']")
  private WebElement signInLink;

  @FindBy(css = "a[href='/user/register']")
  private WebElement signUpLink;

  @FindBy(css = ".nav-link[href*='/profile/']")
  private WebElement profileLink;

  @FindBy(css = ".navbar")
  private WebElement navbar;

  private static final String HOME_URL = "/";

  public HomePage(WebDriver driver) {
    super(driver);
  }

  public HomePage navigateTo(String baseUrl) {
    driver.get(baseUrl + HOME_URL);
    waitForPageLoad();
    return this;
  }

  public void waitForPageLoad() {
    wait.until(ExpectedConditions.visibilityOf(navbar));
  }

  public boolean isUserLoggedIn() {
    try {
      wait.until(ExpectedConditions.visibilityOf(settingsLink));
      return isDisplayed(settingsLink) && isDisplayed(newArticleLink);
    } catch (Exception e) {
      return false;
    }
  }

  public boolean isUserLoggedOut() {
    try {
      return isDisplayed(signInLink) && isDisplayed(signUpLink);
    } catch (Exception e) {
      return false;
    }
  }

  public String getLoggedInUsername() {
    try {
      wait.until(ExpectedConditions.visibilityOf(profileLink));
      String href = profileLink.getAttribute("href");
      return href.substring(href.lastIndexOf("/") + 1);
    } catch (Exception e) {
      return null;
    }
  }

  public boolean isNewArticleLinkDisplayed() {
    try {
      return isDisplayed(newArticleLink);
    } catch (Exception e) {
      return false;
    }
  }

  public boolean isSettingsLinkDisplayed() {
    try {
      return isDisplayed(settingsLink);
    } catch (Exception e) {
      return false;
    }
  }

  public void clickNewArticle() {
    click(newArticleLink);
  }

  public void clickSettings() {
    click(settingsLink);
  }

  public void clickSignIn() {
    click(signInLink);
  }

  public void clickSignUp() {
    click(signUpLink);
  }

  public String getCurrentUrl() {
    return driver.getCurrentUrl();
  }

  public boolean isOnHomePage(String baseUrl) {
    String currentUrl = driver.getCurrentUrl();
    return currentUrl.equals(baseUrl) || currentUrl.equals(baseUrl + "/");
  }

  public String getJwtTokenFromLocalStorage() {
    try {
      JavascriptExecutor js = (JavascriptExecutor) driver;
      Object token = js.executeScript("return localStorage.getItem('jwtToken');");
      return token != null ? token.toString() : null;
    } catch (Exception e) {
      return null;
    }
  }

  public boolean hasJwtToken() {
    String token = getJwtTokenFromLocalStorage();
    return token != null && !token.isEmpty();
  }

  public void clearLocalStorage() {
    try {
      JavascriptExecutor js = (JavascriptExecutor) driver;
      js.executeScript("localStorage.clear();");
    } catch (Exception e) {
      // Ignore errors
    }
  }
}

package io.spring.selenium.pages;

import java.util.List;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class HomePage extends BasePage {

  @FindBy(css = ".navbar-brand")
  private WebElement navbarBrand;

  @FindBy(css = ".nav-link[href='/user/login']")
  private WebElement signInLink;

  @FindBy(css = ".nav-link[href='/user/register']")
  private WebElement signUpLink;

  @FindBy(css = ".article-preview")
  private List<WebElement> articlePreviews;

  @FindBy(css = ".article-preview .author")
  private List<WebElement> authorLinks;

  @FindBy(css = ".nav-link[href='/settings']")
  private WebElement settingsLink;

  @FindBy(css = ".feed-toggle")
  private WebElement feedToggle;

  @FindBy(css = ".tag-list")
  private WebElement tagList;

  private static final String BASE_URL = "http://localhost:3000";

  public HomePage(WebDriver driver) {
    super(driver);
  }

  public void navigateToHomePage() {
    driver.get(BASE_URL);
    waitForPageLoad();
  }

  public void waitForPageLoad() {
    wait.until(ExpectedConditions.visibilityOf(navbarBrand));
  }

  public boolean isHomePageDisplayed() {
    try {
      return navbarBrand.isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public void clickSignIn() {
    click(signInLink);
  }

  public void clickSignUp() {
    click(signUpLink);
  }

  public boolean isSignInLinkDisplayed() {
    try {
      return signInLink.isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public boolean isSignUpLinkDisplayed() {
    try {
      return signUpLink.isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public boolean isSettingsLinkDisplayed() {
    try {
      return settingsLink.isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public boolean isUserLoggedIn() {
    return isSettingsLinkDisplayed();
  }

  public int getArticleCount() {
    try {
      return articlePreviews.size();
    } catch (Exception e) {
      return 0;
    }
  }

  public void clickFirstAuthorLink() {
    if (!authorLinks.isEmpty()) {
      click(authorLinks.get(0));
    }
  }

  public String getFirstAuthorName() {
    if (!authorLinks.isEmpty()) {
      return getText(authorLinks.get(0));
    }
    return "";
  }

  public void clickAuthorLinkByIndex(int index) {
    if (index < authorLinks.size()) {
      click(authorLinks.get(index));
    }
  }

  public String getCurrentUrl() {
    return driver.getCurrentUrl();
  }

  public void clearLocalStorage() {
    try {
      org.openqa.selenium.JavascriptExecutor js = (org.openqa.selenium.JavascriptExecutor) driver;
      js.executeScript("window.localStorage.clear();");
    } catch (Exception e) {
      // Ignore if localStorage is not available
    }
  }

  public void clearSessionStorage() {
    try {
      org.openqa.selenium.JavascriptExecutor js = (org.openqa.selenium.JavascriptExecutor) driver;
      js.executeScript("window.sessionStorage.clear();");
    } catch (Exception e) {
      // Ignore if sessionStorage is not available
    }
  }

  public void clearAllStorage() {
    clearLocalStorage();
    clearSessionStorage();
  }

  public void logout() {
    clearAllStorage();
    driver.navigate().refresh();
    waitForPageLoad();
  }
}

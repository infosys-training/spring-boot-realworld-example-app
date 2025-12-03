package io.spring.selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class HomePage extends BasePage {

  @FindBy(css = ".navbar-brand")
  private WebElement navbarBrand;

  @FindBy(css = ".nav-link[href='/login']")
  private WebElement signInLink;

  @FindBy(css = ".nav-link[href='/register']")
  private WebElement signUpLink;

  @FindBy(css = ".nav-link[href='/editor']")
  private WebElement newArticleLink;

  @FindBy(css = ".nav-link[href='/settings']")
  private WebElement settingsLink;

  public HomePage(WebDriver driver) {
    super(driver);
  }

  public HomePage navigateTo(String baseUrl) {
    driver.get(baseUrl);
    waitForPageLoad();
    return this;
  }

  public void waitForPageLoad() {
    wait.until(ExpectedConditions.visibilityOf(navbarBrand));
  }

  public boolean isLoggedIn() {
    try {
      return !isDisplayed(signInLink);
    } catch (Exception e) {
      return true;
    }
  }

  public void clickSignIn() {
    click(signInLink);
  }

  public void clickSignUp() {
    click(signUpLink);
  }

  public void clickNewArticle() {
    click(newArticleLink);
  }

  public void clickSettings() {
    click(settingsLink);
  }

  public void clickProfile(String username) {
    try {
      WebElement profileLink =
          driver.findElement(By.cssSelector(".nav-link[href='/profile/" + username + "']"));
      click(profileLink);
    } catch (Exception e) {
      // Profile link might not be visible
    }
  }

  public String getCurrentUsername() {
    try {
      WebElement usernameLink = driver.findElement(By.cssSelector(".nav-link.username"));
      return usernameLink.getText();
    } catch (Exception e) {
      return "";
    }
  }

  public boolean isSignInLinkVisible() {
    return isDisplayed(signInLink);
  }

  public boolean isSignUpLinkVisible() {
    return isDisplayed(signUpLink);
  }

  public boolean isNewArticleLinkVisible() {
    try {
      return isDisplayed(newArticleLink);
    } catch (Exception e) {
      return false;
    }
  }

  public boolean isSettingsLinkVisible() {
    try {
      return isDisplayed(settingsLink);
    } catch (Exception e) {
      return false;
    }
  }
}

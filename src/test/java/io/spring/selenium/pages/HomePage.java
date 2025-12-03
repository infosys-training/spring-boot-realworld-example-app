package io.spring.selenium.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/** Page Object for the Home page. */
public class HomePage extends BasePage {

  @FindBy(css = "a[href='/settings']")
  private WebElement settingsLink;

  @FindBy(css = "a[href='/editor']")
  private WebElement newArticleLink;

  @FindBy(css = ".navbar-brand")
  private WebElement navbarBrand;

  @FindBy(css = "a[href='/login']")
  private WebElement loginLink;

  @FindBy(css = "a[href='/register']")
  private WebElement registerLink;

  @FindBy(css = ".nav-link.active")
  private WebElement activeNavLink;

  public HomePage(WebDriver driver) {
    super(driver);
  }

  public HomePage navigateTo(String baseUrl) {
    driver.get(baseUrl);
    waitForVisibility(navbarBrand);
    return this;
  }

  public boolean isUserLoggedIn() {
    try {
      return isDisplayed(settingsLink) || isDisplayed(newArticleLink);
    } catch (Exception e) {
      return false;
    }
  }

  public boolean isLoginLinkDisplayed() {
    return isDisplayed(loginLink);
  }

  public boolean isRegisterLinkDisplayed() {
    return isDisplayed(registerLink);
  }

  public boolean isSettingsLinkDisplayed() {
    return isDisplayed(settingsLink);
  }

  public boolean isNewArticleLinkDisplayed() {
    return isDisplayed(newArticleLink);
  }

  public String getNavbarBrandText() {
    return getText(navbarBrand);
  }

  public void clickLoginLink() {
    click(loginLink);
  }

  public void clickRegisterLink() {
    click(registerLink);
  }

  public void clickSettingsLink() {
    click(settingsLink);
  }

  public void clickNewArticleLink() {
    click(newArticleLink);
  }

  public String getCurrentUrl() {
    return driver.getCurrentUrl();
  }

  public boolean isOnHomePage(String baseUrl) {
    String currentUrl = driver.getCurrentUrl();
    return currentUrl.equals(baseUrl) || currentUrl.equals(baseUrl + "/");
  }
}

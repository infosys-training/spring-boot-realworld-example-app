package io.spring.selenium.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class HomePage extends BasePage {

  @FindBy(css = ".home-page")
  private WebElement homePageContainer;

  @FindBy(css = ".banner h1")
  private WebElement bannerTitle;

  @FindBy(css = ".navbar-brand")
  private WebElement navbarBrand;

  @FindBy(css = ".feed-toggle")
  private WebElement feedToggle;

  @FindBy(linkText = "Your Feed")
  private WebElement yourFeedTab;

  @FindBy(linkText = "Global Feed")
  private WebElement globalFeedTab;

  @FindBy(css = ".sidebar")
  private WebElement sidebar;

  @FindBy(css = ".article-preview")
  private WebElement articlePreview;

  public HomePage(WebDriver driver) {
    super(driver);
  }

  public HomePage navigateTo(String baseUrl) {
    driver.get(baseUrl);
    waitForPageLoad();
    return this;
  }

  public void waitForPageLoad() {
    wait.until(ExpectedConditions.visibilityOf(homePageContainer));
  }

  public void waitForHomePageAfterLogin() {
    wait.until(ExpectedConditions.urlContains("/"));
    wait.until(ExpectedConditions.visibilityOf(homePageContainer));
  }

  public boolean isHomePageDisplayed() {
    try {
      return isDisplayed(homePageContainer);
    } catch (Exception e) {
      return false;
    }
  }

  public String getBannerTitle() {
    try {
      return getText(bannerTitle);
    } catch (Exception e) {
      return "";
    }
  }

  public boolean isYourFeedTabDisplayed() {
    try {
      return isDisplayed(yourFeedTab);
    } catch (Exception e) {
      return false;
    }
  }

  public boolean isGlobalFeedTabDisplayed() {
    try {
      return isDisplayed(globalFeedTab);
    } catch (Exception e) {
      return false;
    }
  }

  public void clickYourFeedTab() {
    click(yourFeedTab);
  }

  public void clickGlobalFeedTab() {
    click(globalFeedTab);
  }

  public boolean isSidebarDisplayed() {
    return isDisplayed(sidebar);
  }

  public String getCurrentUrl() {
    return driver.getCurrentUrl();
  }

  public String getPageTitle() {
    return driver.getTitle();
  }

  public void refresh() {
    driver.navigate().refresh();
    waitForPageLoad();
  }

  public void navigateBack() {
    driver.navigate().back();
  }

  public void navigateForward() {
    driver.navigate().forward();
  }
}

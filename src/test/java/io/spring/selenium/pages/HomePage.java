package io.spring.selenium.pages;

import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class HomePage extends BasePage {

  @FindBy(css = ".nav.nav-pills.outline-active")
  private WebElement tabList;

  @FindBy(linkText = "Your Feed")
  private WebElement yourFeedTab;

  @FindBy(linkText = "Global Feed")
  private WebElement globalFeedTab;

  @FindBy(css = ".article-preview")
  private List<WebElement> articlePreviews;

  @FindBy(css = ".feed-toggle")
  private WebElement feedToggle;

  @FindBy(css = ".pagination")
  private WebElement pagination;

  @FindBy(css = ".pagination .page-item")
  private List<WebElement> paginationItems;

  @FindBy(css = ".sidebar")
  private WebElement sidebar;

  @FindBy(css = ".tag-list .tag-pill")
  private List<WebElement> popularTags;

  @FindBy(css = "nav.navbar")
  private WebElement navbar;

  @FindBy(css = ".banner h1")
  private WebElement bannerTitle;

  public HomePage(WebDriver driver) {
    super(driver);
  }

  public HomePage navigateTo(String baseUrl) {
    driver.get(baseUrl);
    return this;
  }

  public boolean isYourFeedTabVisible() {
    try {
      return isDisplayed(yourFeedTab);
    } catch (Exception e) {
      return false;
    }
  }

  public boolean isGlobalFeedTabVisible() {
    try {
      return isDisplayed(globalFeedTab);
    } catch (Exception e) {
      return false;
    }
  }

  public FeedPage clickYourFeedTab() {
    click(yourFeedTab);
    return new FeedPage(driver);
  }

  public FeedPage clickGlobalFeedTab() {
    click(globalFeedTab);
    return new FeedPage(driver);
  }

  public boolean isYourFeedTabActive() {
    try {
      String classes = yourFeedTab.getAttribute("class");
      return classes != null && classes.contains("active");
    } catch (Exception e) {
      return false;
    }
  }

  public boolean isGlobalFeedTabActive() {
    try {
      String classes = globalFeedTab.getAttribute("class");
      return classes != null && classes.contains("active");
    } catch (Exception e) {
      return false;
    }
  }

  public int getArticleCount() {
    try {
      wait.until(
          ExpectedConditions.or(
              ExpectedConditions.presenceOfElementLocated(By.cssSelector(".article-preview")),
              ExpectedConditions.presenceOfElementLocated(
                  By.xpath("//*[contains(text(), 'No articles')]"))));
      return articlePreviews.size();
    } catch (Exception e) {
      return 0;
    }
  }

  public List<WebElement> getArticlePreviews() {
    return articlePreviews;
  }

  public boolean isPaginationVisible() {
    try {
      return isDisplayed(pagination);
    } catch (Exception e) {
      return false;
    }
  }

  public int getPaginationPageCount() {
    try {
      return paginationItems.size();
    } catch (Exception e) {
      return 0;
    }
  }

  public void clickPaginationPage(int pageNumber) {
    try {
      WebElement pageLink =
          driver.findElement(
              By.cssSelector(".pagination .page-item:nth-child(" + pageNumber + ") a"));
      click(pageLink);
    } catch (Exception e) {
      throw new RuntimeException("Could not click pagination page " + pageNumber, e);
    }
  }

  public boolean isLoggedIn() {
    try {
      return isYourFeedTabVisible();
    } catch (Exception e) {
      return false;
    }
  }

  public void clickTag(String tagName) {
    try {
      WebElement tag =
          driver.findElement(
              By.xpath("//a[contains(@class, 'tag-pill') and text()='" + tagName + "']"));
      click(tag);
    } catch (Exception e) {
      throw new RuntimeException("Could not click tag: " + tagName, e);
    }
  }

  public boolean isTagTabVisible(String tagName) {
    try {
      WebElement tagTab =
          driver.findElement(
              By.xpath(
                  "//ul[contains(@class, 'nav-pills')]//a[contains(text(), '" + tagName + "')]"));
      return isDisplayed(tagTab);
    } catch (Exception e) {
      return false;
    }
  }

  public String getCurrentUrl() {
    return driver.getCurrentUrl();
  }

  public void waitForPageLoad() {
    wait.until(
        ExpectedConditions.or(
            ExpectedConditions.presenceOfElementLocated(By.cssSelector(".article-preview")),
            ExpectedConditions.presenceOfElementLocated(
                By.xpath("//*[contains(text(), 'No articles')]")),
            ExpectedConditions.presenceOfElementLocated(By.cssSelector(".feed-toggle"))));
  }

  public boolean isEmptyFeedMessageDisplayed() {
    try {
      WebElement emptyMessage =
          driver.findElement(By.xpath("//*[contains(text(), 'No articles are here')]"));
      return isDisplayed(emptyMessage);
    } catch (Exception e) {
      return false;
    }
  }

  public boolean isLoadingSpinnerDisplayed() {
    try {
      WebElement spinner = driver.findElement(By.cssSelector(".loading-spinner, .spinner"));
      return isDisplayed(spinner);
    } catch (Exception e) {
      return false;
    }
  }

  public boolean isErrorMessageDisplayed() {
    try {
      WebElement errorMsg = driver.findElement(By.xpath("//*[contains(text(), 'Cannot load')]"));
      return isDisplayed(errorMsg);
    } catch (Exception e) {
      return false;
    }
  }
}

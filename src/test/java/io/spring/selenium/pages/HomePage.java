package io.spring.selenium.pages;

import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class HomePage extends BasePage {

  private static final String BASE_URL = "http://localhost:3000";

  @FindBy(css = ".home-page")
  private WebElement homePageContainer;

  @FindBy(css = ".banner")
  private WebElement banner;

  @FindBy(css = ".feed-toggle")
  private WebElement feedToggle;

  @FindBy(css = ".nav-pills")
  private WebElement tabList;

  @FindBy(css = ".sidebar")
  private WebElement sidebar;

  @FindBy(css = ".sidebar .tag-list")
  private WebElement popularTags;

  @FindBy(xpath = "//a[contains(text(),'Global Feed')]")
  private WebElement globalFeedTab;

  @FindBy(xpath = "//a[contains(text(),'Your Feed')]")
  private WebElement yourFeedTab;

  @FindBy(css = ".article-preview")
  private List<WebElement> articlePreviews;

  @FindBy(css = ".pagination")
  private WebElement pagination;

  @FindBy(css = ".error-message")
  private WebElement errorMessage;

  @FindBy(css = ".loading-spinner")
  private WebElement loadingSpinner;

  public HomePage(WebDriver driver) {
    super(driver);
  }

  public HomePage navigate() {
    driver.get(BASE_URL);
    waitForPageLoad();
    return this;
  }

  public HomePage navigateWithTag(String tag) {
    driver.get(BASE_URL + "/?tag=" + tag);
    waitForPageLoad();
    return this;
  }

  public void waitForPageLoad() {
    wait.until(ExpectedConditions.visibilityOf(homePageContainer));
    waitForArticlesOrEmptyState();
  }

  private void waitForArticlesOrEmptyState() {
    try {
      wait.until(
          driver ->
              !articlePreviews.isEmpty()
                  || driver.findElements(By.cssSelector(".article-preview")).stream()
                      .anyMatch(e -> e.getText().contains("No articles")));
    } catch (Exception e) {
      // Page might have error state
    }
  }

  public boolean isHomePageDisplayed() {
    try {
      return homePageContainer.isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public boolean isBannerDisplayed() {
    try {
      return banner.isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public boolean isGlobalFeedTabDisplayed() {
    try {
      return globalFeedTab.isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public boolean isYourFeedTabDisplayed() {
    try {
      return yourFeedTab.isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public void clickGlobalFeedTab() {
    click(globalFeedTab);
    waitForArticlesOrEmptyState();
  }

  public void clickYourFeedTab() {
    click(yourFeedTab);
    waitForArticlesOrEmptyState();
  }

  public int getArticleCount() {
    return articlePreviews.size();
  }

  public List<WebElement> getArticlePreviews() {
    return articlePreviews;
  }

  public WebElement getArticlePreviewAt(int index) {
    if (index >= 0 && index < articlePreviews.size()) {
      return articlePreviews.get(index);
    }
    return null;
  }

  public boolean hasArticles() {
    return !articlePreviews.isEmpty()
        && !articlePreviews.get(0).getText().contains("No articles are here");
  }

  public boolean isEmptyStateDisplayed() {
    try {
      return articlePreviews.stream().anyMatch(e -> e.getText().contains("No articles are here"));
    } catch (Exception e) {
      return false;
    }
  }

  public boolean isErrorMessageDisplayed() {
    try {
      return errorMessage.isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public String getErrorMessageText() {
    try {
      return getText(errorMessage);
    } catch (Exception e) {
      return "";
    }
  }

  public boolean isPaginationDisplayed() {
    try {
      return pagination.isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public WebElement getPagination() {
    return pagination;
  }

  public boolean isSidebarDisplayed() {
    try {
      return sidebar.isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public List<WebElement> getPopularTags() {
    try {
      return popularTags.findElements(By.cssSelector(".tag-pill"));
    } catch (Exception e) {
      return List.of();
    }
  }

  public void clickTag(String tagName) {
    List<WebElement> tags = getPopularTags();
    for (WebElement tag : tags) {
      if (tag.getText().equals(tagName)) {
        click(tag);
        waitForArticlesOrEmptyState();
        return;
      }
    }
  }

  public boolean isLoadingSpinnerDisplayed() {
    try {
      return loadingSpinner.isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public String getPageTitle() {
    return driver.getTitle();
  }

  public String getCurrentUrl() {
    return driver.getCurrentUrl();
  }
}

package io.spring.selenium.pages;

import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class HomePage extends BasePage {

  @FindBy(css = ".navbar-brand")
  private WebElement navbarBrand;

  @FindBy(css = ".banner h1")
  private WebElement bannerTitle;

  @FindBy(css = ".nav-pills .nav-link")
  private List<WebElement> feedTabs;

  @FindBy(css = ".article-preview")
  private List<WebElement> articlePreviews;

  @FindBy(css = ".sidebar .tag-list .tag-pill")
  private List<WebElement> popularTags;

  @FindBy(css = ".pagination .page-item")
  private List<WebElement> paginationItems;

  @FindBy(css = "a[href='/user/login']")
  private WebElement signInLink;

  @FindBy(css = "a[href='/user/register']")
  private WebElement signUpLink;

  @FindBy(css = ".nav-link[href='/editor']")
  private WebElement newArticleLink;

  @FindBy(css = ".nav-link[href='/user/settings']")
  private WebElement settingsLink;

  public HomePage(WebDriver driver) {
    super(driver);
  }

  public void navigateTo(String baseUrl) {
    driver.get(baseUrl);
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

  public String getBannerTitle() {
    return getText(bannerTitle);
  }

  public List<WebElement> getFeedTabs() {
    return feedTabs;
  }

  public void clickFeedTab(String tabName) {
    for (WebElement tab : feedTabs) {
      if (tab.getText().contains(tabName)) {
        click(tab);
        break;
      }
    }
  }

  public List<WebElement> getArticlePreviews() {
    return articlePreviews;
  }

  public int getArticleCount() {
    return articlePreviews.size();
  }

  public void clickArticle(int index) {
    if (index < articlePreviews.size()) {
      WebElement article = articlePreviews.get(index);
      WebElement titleLink = article.findElement(By.cssSelector("a.preview-link h1"));
      click(titleLink);
    }
  }

  public String getArticleTitle(int index) {
    if (index < articlePreviews.size()) {
      WebElement article = articlePreviews.get(index);
      return article.findElement(By.cssSelector("a.preview-link h1")).getText();
    }
    return null;
  }

  public List<WebElement> getPopularTags() {
    return popularTags;
  }

  public void clickTag(String tagName) {
    for (WebElement tag : popularTags) {
      if (tag.getText().equals(tagName)) {
        click(tag);
        break;
      }
    }
  }

  public void clickSignIn() {
    click(signInLink);
  }

  public void clickSignUp() {
    click(signUpLink);
  }

  public boolean isUserLoggedIn() {
    try {
      return !signInLink.isDisplayed();
    } catch (Exception e) {
      return true;
    }
  }

  public void clickNewArticle() {
    click(newArticleLink);
  }

  public void clickSettings() {
    click(settingsLink);
  }

  public void navigateToProfile(String username) {
    driver.get(
        driver.getCurrentUrl().split("/")[0]
            + "//"
            + driver.getCurrentUrl().split("/")[2]
            + "/profile/"
            + username);
  }

  public List<WebElement> getPaginationItems() {
    return paginationItems;
  }

  public void clickPaginationPage(int pageNumber) {
    for (WebElement item : paginationItems) {
      if (item.getText().equals(String.valueOf(pageNumber))) {
        click(item);
        break;
      }
    }
  }

  public boolean isPaginationDisplayed() {
    return !paginationItems.isEmpty();
  }
}

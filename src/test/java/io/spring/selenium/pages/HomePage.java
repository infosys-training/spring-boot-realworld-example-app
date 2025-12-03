package io.spring.selenium.pages;

import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class HomePage extends BasePage {

  @FindBy(css = ".home-page")
  private WebElement homePageContainer;

  @FindBy(css = ".banner h1")
  private WebElement bannerTitle;

  @FindBy(css = ".article-preview")
  private List<WebElement> articlePreviews;

  @FindBy(css = ".nav-pills .nav-link")
  private List<WebElement> feedTabs;

  @FindBy(css = ".sidebar .tag-list .tag-pill")
  private List<WebElement> popularTags;

  @FindBy(css = ".pagination .page-item")
  private List<WebElement> paginationItems;

  @FindBy(css = "nav a[href='/']")
  private WebElement homeLink;

  @FindBy(css = "nav a[href='/user/login']")
  private WebElement signInLink;

  @FindBy(css = "nav a[href='/user/register']")
  private WebElement signUpLink;

  public HomePage(WebDriver driver) {
    super(driver);
  }

  public void navigateTo(String baseUrl) {
    driver.get(baseUrl);
    waitForPageLoad();
  }

  public void waitForPageLoad() {
    wait.until(ExpectedConditions.visibilityOf(homePageContainer));
  }

  public boolean isHomePageDisplayed() {
    return isDisplayed(homePageContainer);
  }

  public String getBannerTitle() {
    return getText(bannerTitle);
  }

  public List<WebElement> getArticlePreviews() {
    return articlePreviews;
  }

  public int getArticleCount() {
    return articlePreviews.size();
  }

  public void clickOnAuthorLink(String authorName) {
    WebElement authorLink = driver.findElement(By.cssSelector(".article-preview .author span"));
    for (WebElement preview : articlePreviews) {
      WebElement author = preview.findElement(By.cssSelector(".author span"));
      if (author.getText().equals(authorName)) {
        click(author);
        return;
      }
    }
  }

  public void clickOnArticleTitle(int index) {
    if (index < articlePreviews.size()) {
      WebElement titleLink = articlePreviews.get(index).findElement(By.cssSelector("h1"));
      click(titleLink);
    }
  }

  public String getArticleAuthor(int index) {
    if (index < articlePreviews.size()) {
      WebElement author = articlePreviews.get(index).findElement(By.cssSelector(".author span"));
      return author.getText();
    }
    return null;
  }

  public void clickTag(String tagName) {
    for (WebElement tag : popularTags) {
      if (tag.getText().equals(tagName)) {
        click(tag);
        return;
      }
    }
  }

  public void clickSignIn() {
    click(signInLink);
  }

  public void clickSignUp() {
    click(signUpLink);
  }

  public boolean hasPagination() {
    return !paginationItems.isEmpty();
  }

  public void clickPaginationPage(int pageNumber) {
    for (WebElement item : paginationItems) {
      if (item.getText().equals(String.valueOf(pageNumber))) {
        click(item);
        return;
      }
    }
  }
}

package io.spring.selenium.pages;

import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class HomePage extends BasePage {

  @FindBy(css = ".article-preview")
  private List<WebElement> articlePreviews;

  @FindBy(css = ".navbar-brand")
  private WebElement navbarBrand;

  @FindBy(css = "a[href='/user/login']")
  private WebElement signInLink;

  @FindBy(css = "a[href='/user/register']")
  private WebElement signUpLink;

  @FindBy(css = ".nav-link[href='/user/settings']")
  private WebElement settingsLink;

  @FindBy(css = ".feed-toggle")
  private WebElement feedToggle;

  @FindBy(css = ".tag-list")
  private WebElement tagList;

  @FindBy(css = ".pagination")
  private WebElement pagination;

  public HomePage(WebDriver driver) {
    super(driver);
  }

  public void navigateTo(String baseUrl) {
    driver.get(baseUrl);
  }

  public boolean isOnHomePage() {
    try {
      return waitForVisibility(navbarBrand).isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public boolean isLoggedIn() {
    try {
      return isDisplayed(settingsLink);
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

  public int getArticleCount() {
    try {
      wait.until(
          ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(".article-preview")));
      return articlePreviews.size();
    } catch (Exception e) {
      return 0;
    }
  }

  public List<WebElement> getArticlePreviews() {
    wait.until(
        ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(".article-preview")));
    return articlePreviews;
  }

  public WebElement getFirstArticlePreview() {
    List<WebElement> previews = getArticlePreviews();
    if (previews.isEmpty()) {
      return null;
    }
    return previews.get(0);
  }

  public WebElement getArticlePreviewByIndex(int index) {
    List<WebElement> previews = getArticlePreviews();
    if (index < 0 || index >= previews.size()) {
      return null;
    }
    return previews.get(index);
  }

  public WebElement getFavoriteButtonForArticle(int index) {
    WebElement preview = getArticlePreviewByIndex(index);
    if (preview == null) {
      return null;
    }
    return preview.findElement(By.cssSelector(".btn"));
  }

  public void clickFavoriteButtonForArticle(int index) {
    WebElement favoriteButton = getFavoriteButtonForArticle(index);
    if (favoriteButton != null) {
      click(favoriteButton);
    }
  }

  public int getFavoriteCountForArticle(int index) {
    WebElement favoriteButton = getFavoriteButtonForArticle(index);
    if (favoriteButton == null) {
      return -1;
    }
    String text = favoriteButton.getText().trim();
    try {
      return Integer.parseInt(text);
    } catch (NumberFormatException e) {
      return 0;
    }
  }

  public boolean isArticleFavorited(int index) {
    WebElement favoriteButton = getFavoriteButtonForArticle(index);
    if (favoriteButton == null) {
      return false;
    }
    String className = favoriteButton.getAttribute("class");
    return className.contains("btn-primary") && !className.contains("btn-outline-primary");
  }

  public String getArticleTitleByIndex(int index) {
    WebElement preview = getArticlePreviewByIndex(index);
    if (preview == null) {
      return null;
    }
    try {
      WebElement titleElement = preview.findElement(By.cssSelector("h1"));
      return titleElement.getText();
    } catch (Exception e) {
      return null;
    }
  }

  public String getArticleSlugByIndex(int index) {
    WebElement preview = getArticlePreviewByIndex(index);
    if (preview == null) {
      return null;
    }
    try {
      WebElement link = preview.findElement(By.cssSelector("a.preview-link"));
      String href = link.getAttribute("href");
      return href.substring(href.lastIndexOf("/") + 1);
    } catch (Exception e) {
      return null;
    }
  }

  public void clickArticleByIndex(int index) {
    WebElement preview = getArticlePreviewByIndex(index);
    if (preview != null) {
      WebElement link = preview.findElement(By.cssSelector("a.preview-link"));
      click(link);
    }
  }

  public void waitForArticlesToLoad() {
    wait.until(
        ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(".article-preview")));
  }

  public void waitForPageToLoad() {
    wait.until(ExpectedConditions.visibilityOf(navbarBrand));
  }
}

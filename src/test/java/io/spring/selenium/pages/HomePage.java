package io.spring.selenium.pages;

import java.util.List;
import java.util.stream.Collectors;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

/** Page Object for the Home page with article listings. */
public class HomePage extends BasePage {

  @FindBy(css = ".article-preview")
  private List<WebElement> articlePreviews;

  @FindBy(css = ".article-preview h1")
  private List<WebElement> articleTitles;

  @FindBy(css = ".article-preview a")
  private List<WebElement> articleLinks;

  @FindBy(css = ".tag-list .tag-pill")
  private List<WebElement> popularTags;

  @FindBy(css = ".nav-link")
  private List<WebElement> navLinks;

  @FindBy(css = "a[href='/user/login']")
  private WebElement signInLink;

  @FindBy(css = "a[href='/user/register']")
  private WebElement signUpLink;

  @FindBy(css = ".feed-toggle")
  private WebElement feedToggle;

  private static final String BASE_URL = "http://localhost:3000";

  public HomePage(WebDriver driver) {
    super(driver);
  }

  public void navigateToHomePage() {
    driver.get(BASE_URL);
    waitForPageLoad();
  }

  public void waitForPageLoad() {
    try {
      wait.until(
          ExpectedConditions.or(
              ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".article-preview")),
              ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".home-page")),
              ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".banner"))));
    } catch (Exception e) {
      // Page may have loaded with different structure
    }
  }

  public int getArticleCount() {
    return articlePreviews.size();
  }

  public void clickFirstArticle() {
    if (!articleLinks.isEmpty()) {
      click(articleLinks.get(0));
    }
  }

  public void clickArticleByIndex(int index) {
    if (index < articleLinks.size()) {
      click(articleLinks.get(index));
    }
  }

  public String getFirstArticleTitle() {
    if (!articleTitles.isEmpty()) {
      return getText(articleTitles.get(0));
    }
    return "";
  }

  public void clickSignIn() {
    click(signInLink);
  }

  public void clickSignUp() {
    click(signUpLink);
  }

  public boolean isSignInLinkDisplayed() {
    try {
      return isDisplayed(signInLink);
    } catch (Exception e) {
      return false;
    }
  }

  public void clickTag(String tagName) {
    for (WebElement tag : popularTags) {
      if (getText(tag).equalsIgnoreCase(tagName)) {
        click(tag);
        return;
      }
    }
  }

  public List<String> getPopularTags() {
    return popularTags.stream().map(this::getText).collect(Collectors.toList());
  }

  public String getCurrentUrl() {
    return driver.getCurrentUrl();
  }

  public void navigateToArticleBySlug(String slug) {
    driver.get(BASE_URL + "/article/" + slug);
  }
}

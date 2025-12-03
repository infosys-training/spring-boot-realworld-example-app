package io.spring.selenium.pages;

import java.util.List;
import java.util.stream.Collectors;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class HomePage extends BasePage {

  @FindBy(css = ".home-page")
  private WebElement homePageContainer;

  @FindBy(css = ".article-preview")
  private List<WebElement> articlePreviews;

  @FindBy(css = ".article-preview h1 a")
  private List<WebElement> articleTitles;

  @FindBy(css = ".article-preview .preview-link")
  private List<WebElement> articleLinks;

  @FindBy(css = ".tag-list .tag-pill")
  private List<WebElement> popularTags;

  @FindBy(css = ".sidebar .tag-list .tag-pill")
  private List<WebElement> sidebarTags;

  @FindBy(css = ".nav-link")
  private List<WebElement> navLinks;

  @FindBy(css = ".feed-toggle .nav-link")
  private List<WebElement> feedTabs;

  @FindBy(css = ".pagination .page-item")
  private List<WebElement> paginationItems;

  private static final String BASE_URL = "http://localhost:3000";

  public HomePage(WebDriver driver) {
    super(driver);
  }

  public HomePage navigateToHomePage() {
    driver.get(BASE_URL);
    return this;
  }

  public boolean isHomePageDisplayed() {
    try {
      return wait.until(ExpectedConditions.visibilityOf(homePageContainer)).isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public int getArticleCount() {
    try {
      return articlePreviews.size();
    } catch (Exception e) {
      return 0;
    }
  }

  public List<String> getArticleTitles() {
    try {
      return articleTitles.stream().map(WebElement::getText).collect(Collectors.toList());
    } catch (Exception e) {
      return List.of();
    }
  }

  public void clickArticleByTitle(String title) {
    for (WebElement articleTitle : articleTitles) {
      if (articleTitle.getText().equals(title)) {
        click(articleTitle);
        return;
      }
    }
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

  public List<String> getPopularTags() {
    try {
      return sidebarTags.stream().map(WebElement::getText).collect(Collectors.toList());
    } catch (Exception e) {
      return List.of();
    }
  }

  public void clickTag(String tagName) {
    for (WebElement tag : sidebarTags) {
      if (tag.getText().equals(tagName)) {
        click(tag);
        return;
      }
    }
  }

  public boolean isUserLoggedIn() {
    try {
      for (WebElement navLink : navLinks) {
        String text = navLink.getText().toLowerCase();
        if (text.contains("settings") || text.contains("new article")) {
          return true;
        }
      }
      return false;
    } catch (Exception e) {
      return false;
    }
  }

  public void clickSignIn() {
    for (WebElement navLink : navLinks) {
      if (navLink.getText().toLowerCase().contains("sign in")) {
        click(navLink);
        return;
      }
    }
  }

  public void clickSignUp() {
    for (WebElement navLink : navLinks) {
      if (navLink.getText().toLowerCase().contains("sign up")) {
        click(navLink);
        return;
      }
    }
  }

  public void clickNewArticle() {
    for (WebElement navLink : navLinks) {
      if (navLink.getText().toLowerCase().contains("new article")) {
        click(navLink);
        return;
      }
    }
  }

  public void clickGlobalFeed() {
    for (WebElement tab : feedTabs) {
      if (tab.getText().toLowerCase().contains("global")) {
        click(tab);
        return;
      }
    }
  }

  public void clickYourFeed() {
    for (WebElement tab : feedTabs) {
      if (tab.getText().toLowerCase().contains("your feed")) {
        click(tab);
        return;
      }
    }
  }

  public String getCurrentUrl() {
    return driver.getCurrentUrl();
  }

  public void waitForArticlesToLoad() {
    try {
      wait.until(ExpectedConditions.visibilityOfAllElements(articlePreviews));
    } catch (Exception e) {
      // Articles might not be present
    }
  }
}

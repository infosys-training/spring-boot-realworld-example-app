package io.spring.selenium.pages;

import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class ProfilePage extends BasePage {

  @FindBy(css = ".user-info h4")
  private WebElement username;

  @FindBy(css = ".user-info p")
  private WebElement userBio;

  @FindBy(css = ".user-info img")
  private WebElement userImage;

  @FindBy(css = ".nav-link[href*='favorites']")
  private WebElement favoritedArticlesTab;

  @FindBy(css = ".nav-link.active")
  private WebElement activeTab;

  @FindBy(css = ".article-preview")
  private List<WebElement> articlePreviews;

  @FindBy(css = ".btn-outline-secondary")
  private WebElement editProfileButton;

  @FindBy(css = ".btn-outline-primary")
  private WebElement followButton;

  public ProfilePage(WebDriver driver) {
    super(driver);
  }

  public void navigateTo(String baseUrl, String usernameParam) {
    driver.get(baseUrl + "/profile/" + usernameParam);
  }

  public void navigateToFavorites(String baseUrl, String usernameParam) {
    driver.get(baseUrl + "/profile/" + usernameParam + "/favorites");
  }

  public boolean isOnProfilePage() {
    try {
      return waitForVisibility(username).isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public String getUsername() {
    try {
      return getText(username);
    } catch (Exception e) {
      return null;
    }
  }

  public String getUserBio() {
    try {
      return getText(userBio);
    } catch (Exception e) {
      return null;
    }
  }

  public void clickFavoritedArticlesTab() {
    click(favoritedArticlesTab);
  }

  public boolean isFavoritedTabActive() {
    try {
      String activeTabText = getText(activeTab);
      return activeTabText.contains("Favorited");
    } catch (Exception e) {
      return false;
    }
  }

  public int getFavoritedArticleCount() {
    try {
      wait.until(
          ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(".article-preview")));
      return articlePreviews.size();
    } catch (Exception e) {
      return 0;
    }
  }

  public List<WebElement> getFavoritedArticles() {
    try {
      wait.until(
          ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(".article-preview")));
      return articlePreviews;
    } catch (Exception e) {
      return java.util.Collections.emptyList();
    }
  }

  public boolean hasArticleWithTitle(String title) {
    List<WebElement> articles = getFavoritedArticles();
    for (WebElement article : articles) {
      try {
        WebElement titleElement = article.findElement(By.cssSelector("h1"));
        if (titleElement.getText().equals(title)) {
          return true;
        }
      } catch (Exception e) {
        continue;
      }
    }
    return false;
  }

  public boolean hasEditProfileButton() {
    try {
      return isDisplayed(editProfileButton);
    } catch (Exception e) {
      return false;
    }
  }

  public boolean hasFollowButton() {
    try {
      return isDisplayed(followButton);
    } catch (Exception e) {
      return false;
    }
  }

  public void clickFollowButton() {
    click(followButton);
  }

  public void waitForPageToLoad() {
    wait.until(ExpectedConditions.visibilityOf(username));
  }
}

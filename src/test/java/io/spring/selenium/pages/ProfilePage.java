package io.spring.selenium.pages;

import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class ProfilePage extends BasePage {

  @FindBy(css = ".profile-page")
  private WebElement profilePageContainer;

  @FindBy(css = ".user-info h4")
  private WebElement usernameHeader;

  @FindBy(css = ".user-info p")
  private WebElement userBio;

  @FindBy(css = ".user-info .user-img")
  private WebElement userImage;

  @FindBy(css = ".articles-toggle .nav-pills")
  private WebElement articlesToggle;

  @FindBy(css = ".nav-pills .nav-item")
  private List<WebElement> profileTabs;

  @FindBy(css = ".article-preview")
  private List<WebElement> articlePreviews;

  @FindBy(css = ".pagination .page-item")
  private List<WebElement> paginationItems;

  @FindBy(css = ".btn-outline-secondary")
  private WebElement followButton;

  @FindBy(css = ".btn-secondary")
  private WebElement unfollowButton;

  @FindBy(css = "a[href='/settings']")
  private WebElement editProfileButton;

  public ProfilePage(WebDriver driver) {
    super(driver);
  }

  public void navigateTo(String baseUrl, String username) {
    driver.get(baseUrl + "/profile/" + username);
    waitForPageLoad();
  }

  public void navigateToWithAuthorFilter(String baseUrl, String username) {
    driver.get(baseUrl + "/profile/" + username);
    waitForPageLoad();
  }

  public void navigateToWithPagination(String baseUrl, String username, int offset, int limit) {
    driver.get(baseUrl + "/profile/" + username + "?offset=" + offset + "&limit=" + limit);
    waitForPageLoad();
  }

  public void waitForPageLoad() {
    wait.until(ExpectedConditions.visibilityOf(profilePageContainer));
  }

  public void waitForArticlesLoad() {
    wait.until(
        ExpectedConditions.or(
            ExpectedConditions.presenceOfElementLocated(By.cssSelector(".article-preview")),
            ExpectedConditions.presenceOfElementLocated(
                By.xpath("//*[contains(text(), 'No articles')]"))));
  }

  public boolean isProfilePageDisplayed() {
    return isDisplayed(profilePageContainer);
  }

  public String getDisplayedUsername() {
    return getText(usernameHeader);
  }

  public String getUserBio() {
    try {
      return getText(userBio);
    } catch (Exception e) {
      return "";
    }
  }

  public boolean isUserImageDisplayed() {
    return isDisplayed(userImage);
  }

  public List<WebElement> getArticlePreviews() {
    return articlePreviews;
  }

  public int getArticleCount() {
    try {
      waitForArticlesLoad();
      return articlePreviews.size();
    } catch (Exception e) {
      return 0;
    }
  }

  public boolean hasNoArticlesMessage() {
    try {
      WebElement noArticles =
          driver.findElement(By.xpath("//*[contains(text(), 'No articles are here')]"));
      return noArticles.isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public String getArticleTitle(int index) {
    if (index < articlePreviews.size()) {
      WebElement title = articlePreviews.get(index).findElement(By.cssSelector("h1"));
      return title.getText();
    }
    return null;
  }

  public String getArticleAuthor(int index) {
    if (index < articlePreviews.size()) {
      WebElement author = articlePreviews.get(index).findElement(By.cssSelector(".author span"));
      return author.getText();
    }
    return null;
  }

  public String getArticleDescription(int index) {
    if (index < articlePreviews.size()) {
      WebElement description = articlePreviews.get(index).findElement(By.cssSelector("p"));
      return description.getText();
    }
    return null;
  }

  public String getArticleDate(int index) {
    if (index < articlePreviews.size()) {
      WebElement date = articlePreviews.get(index).findElement(By.cssSelector(".date"));
      return date.getText();
    }
    return null;
  }

  public List<String> getArticleTags(int index) {
    if (index < articlePreviews.size()) {
      List<WebElement> tags =
          articlePreviews.get(index).findElements(By.cssSelector(".tag-list li span"));
      return tags.stream().map(WebElement::getText).collect(java.util.stream.Collectors.toList());
    }
    return java.util.Collections.emptyList();
  }

  public int getFavoritesCount(int index) {
    if (index < articlePreviews.size()) {
      WebElement favButton = articlePreviews.get(index).findElement(By.cssSelector("button"));
      String text = favButton.getText().trim();
      try {
        return Integer.parseInt(text);
      } catch (NumberFormatException e) {
        return 0;
      }
    }
    return 0;
  }

  public void clickOnArticle(int index) {
    if (index < articlePreviews.size()) {
      WebElement titleLink = articlePreviews.get(index).findElement(By.cssSelector("h1"));
      click(titleLink);
    }
  }

  public void clickOnAuthorLink(int index) {
    if (index < articlePreviews.size()) {
      WebElement authorLink = articlePreviews.get(index).findElement(By.cssSelector(".author"));
      click(authorLink);
    }
  }

  public void clickMyArticlesTab() {
    for (WebElement tab : profileTabs) {
      if (tab.getText().contains("My Articles")) {
        click(tab);
        return;
      }
    }
  }

  public void clickFavoritedArticlesTab() {
    for (WebElement tab : profileTabs) {
      if (tab.getText().contains("Favorited")) {
        click(tab);
        return;
      }
    }
  }

  public boolean hasPagination() {
    return !paginationItems.isEmpty();
  }

  public int getPaginationPageCount() {
    return paginationItems.size();
  }

  public void clickPaginationPage(int pageNumber) {
    for (WebElement item : paginationItems) {
      WebElement link = item.findElement(By.cssSelector("a"));
      if (link.getText().equals(String.valueOf(pageNumber))) {
        click(link);
        return;
      }
    }
  }

  public void clickNextPage() {
    for (WebElement item : paginationItems) {
      if (item.getText().contains(">") || item.getText().contains("Next")) {
        click(item);
        return;
      }
    }
    if (paginationItems.size() > 1) {
      click(paginationItems.get(paginationItems.size() - 1));
    }
  }

  public void clickPreviousPage() {
    for (WebElement item : paginationItems) {
      if (item.getText().contains("<") || item.getText().contains("Prev")) {
        click(item);
        return;
      }
    }
    if (paginationItems.size() > 1) {
      click(paginationItems.get(0));
    }
  }

  public boolean isFollowButtonDisplayed() {
    try {
      return followButton.isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public void clickFollowButton() {
    click(followButton);
  }

  public boolean allArticlesBelongToAuthor(String expectedAuthor) {
    for (WebElement preview : articlePreviews) {
      WebElement author = preview.findElement(By.cssSelector(".author span"));
      if (!author.getText().equals(expectedAuthor)) {
        return false;
      }
    }
    return true;
  }

  public boolean isCurrentPageActive(int pageNumber) {
    for (WebElement item : paginationItems) {
      if (item.getAttribute("class").contains("active")) {
        WebElement link = item.findElement(By.cssSelector("a"));
        return link.getText().equals(String.valueOf(pageNumber));
      }
    }
    return false;
  }
}

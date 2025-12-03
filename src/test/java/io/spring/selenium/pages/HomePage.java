package io.spring.selenium.pages;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class HomePage extends BasePage {

  private static final String BASE_URL = "http://localhost:3000";

  @FindBy(css = ".sidebar")
  private WebElement sidebar;

  @FindBy(css = ".tag-list")
  private WebElement tagList;

  @FindBy(css = ".tag-list .tag-pill")
  private List<WebElement> tagPills;

  @FindBy(css = ".banner h1")
  private WebElement bannerTitle;

  @FindBy(css = ".article-preview")
  private List<WebElement> articlePreviews;

  @FindBy(css = ".nav-link")
  private List<WebElement> navLinks;

  @FindBy(css = "a[href='/login']")
  private WebElement signInLink;

  @FindBy(css = "a[href='/register']")
  private WebElement signUpLink;

  @FindBy(css = "a[href='/editor/new']")
  private WebElement newArticleLink;

  @FindBy(css = ".error-message")
  private WebElement errorMessage;

  @FindBy(css = ".loading-spinner, .spinner")
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
    wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".home-page")));
  }

  public void waitForTagsToLoad() {
    try {
      wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".tag-list")));
    } catch (Exception e) {
      // Tags might be empty, which is valid
    }
  }

  public boolean isSidebarDisplayed() {
    try {
      return sidebar.isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public boolean isTagListDisplayed() {
    try {
      return tagList.isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public List<String> getAllTags() {
    waitForTagsToLoad();
    List<String> tags = new ArrayList<>();
    try {
      List<WebElement> elements = driver.findElements(By.cssSelector(".tag-list .tag-pill"));
      for (WebElement element : elements) {
        String tagText = element.getText().trim();
        if (!tagText.isEmpty()) {
          tags.add(tagText);
        }
      }
    } catch (Exception e) {
      // Return empty list if no tags found
    }
    return tags;
  }

  public int getTagCount() {
    return getAllTags().size();
  }

  public boolean hasTag(String tagName) {
    List<String> tags = getAllTags();
    return tags.contains(tagName);
  }

  public boolean hasDuplicateTags() {
    List<String> tags = getAllTags();
    Set<String> uniqueTags = new HashSet<>(tags);
    return tags.size() != uniqueTags.size();
  }

  public Set<String> getDuplicateTags() {
    List<String> tags = getAllTags();
    Set<String> seen = new HashSet<>();
    Set<String> duplicates = new HashSet<>();
    for (String tag : tags) {
      if (!seen.add(tag)) {
        duplicates.add(tag);
      }
    }
    return duplicates;
  }

  public void clickTag(String tagName) {
    waitForTagsToLoad();
    List<WebElement> elements = driver.findElements(By.cssSelector(".tag-list .tag-pill"));
    for (WebElement element : elements) {
      if (element.getText().trim().equals(tagName)) {
        click(element);
        return;
      }
    }
    throw new RuntimeException("Tag not found: " + tagName);
  }

  public boolean isTagClickable(String tagName) {
    waitForTagsToLoad();
    List<WebElement> elements = driver.findElements(By.cssSelector(".tag-list .tag-pill"));
    for (WebElement element : elements) {
      if (element.getText().trim().equals(tagName)) {
        try {
          return element.isEnabled() && element.isDisplayed();
        } catch (Exception e) {
          return false;
        }
      }
    }
    return false;
  }

  public boolean areAllTagsStrings() {
    List<String> tags = getAllTags();
    for (String tag : tags) {
      if (tag == null) {
        return false;
      }
      // Check if tag looks like an object representation
      if (tag.startsWith("[object") || tag.startsWith("{")) {
        return false;
      }
    }
    return true;
  }

  public boolean areAllTagsNonEmpty() {
    List<String> tags = getAllTags();
    for (String tag : tags) {
      if (tag == null || tag.trim().isEmpty()) {
        return false;
      }
    }
    return true;
  }

  public boolean areAllTagsNonNumeric() {
    List<String> tags = getAllTags();
    for (String tag : tags) {
      try {
        Double.parseDouble(tag);
        return false; // If parsing succeeds, it's numeric
      } catch (NumberFormatException e) {
        // Not numeric, which is expected
      }
    }
    return true;
  }

  public boolean isTagListContainerPresent() {
    try {
      WebElement container = driver.findElement(By.cssSelector(".tag-list"));
      return container != null;
    } catch (Exception e) {
      return false;
    }
  }

  public boolean isTagListEmpty() {
    return getAllTags().isEmpty();
  }

  public String getBannerTitle() {
    try {
      return getText(bannerTitle);
    } catch (Exception e) {
      return "";
    }
  }

  public boolean isUserLoggedIn() {
    try {
      // If sign in link is not visible, user is logged in
      return !signInLink.isDisplayed();
    } catch (Exception e) {
      return true; // Element not found means user is logged in
    }
  }

  public LoginPage goToLogin() {
    click(signInLink);
    return new LoginPage(driver);
  }

  public RegisterPage goToRegister() {
    click(signUpLink);
    return new RegisterPage(driver);
  }

  public EditorPage goToNewArticle() {
    click(newArticleLink);
    return new EditorPage(driver);
  }

  public boolean hasErrorMessage() {
    try {
      return errorMessage.isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public String getErrorMessage() {
    try {
      return getText(errorMessage);
    } catch (Exception e) {
      return "";
    }
  }

  public int getArticleCount() {
    try {
      return articlePreviews.size();
    } catch (Exception e) {
      return 0;
    }
  }

  public String getPageTitle() {
    return driver.getTitle();
  }

  public String getCurrentUrl() {
    return driver.getCurrentUrl();
  }

  public void refresh() {
    driver.navigate().refresh();
    waitForPageLoad();
  }

  public boolean isPageLoaded() {
    try {
      return driver.findElement(By.cssSelector(".home-page")).isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public WebElement getTagListElement() {
    try {
      return driver.findElement(By.cssSelector(".tag-list"));
    } catch (Exception e) {
      return null;
    }
  }

  public List<WebElement> getTagElements() {
    try {
      return driver.findElements(By.cssSelector(".tag-list .tag-pill"));
    } catch (Exception e) {
      return new ArrayList<>();
    }
  }
}

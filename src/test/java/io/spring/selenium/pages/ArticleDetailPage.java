package io.spring.selenium.pages;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class ArticleDetailPage extends BasePage {

  private static final String ARTICLE_URL_PREFIX = "/article/";

  @FindBy(css = ".banner h1")
  private WebElement articleTitle;

  @FindBy(css = ".article-meta .date")
  private WebElement articleDate;

  @FindBy(css = ".article-meta .author")
  private WebElement authorLink;

  @FindBy(css = ".article-content")
  private WebElement articleContent;

  @FindBy(css = ".tag-list .tag-pill")
  private List<WebElement> tags;

  @FindBy(css = ".article-meta")
  private WebElement articleMeta;

  @FindBy(css = ".btn-outline-secondary")
  private WebElement editButton;

  @FindBy(css = ".btn-outline-danger")
  private WebElement deleteButton;

  @FindBy(css = ".error-page")
  private WebElement errorPage;

  private String createdAtTimestamp;
  private String updatedAtTimestamp;

  public ArticleDetailPage(WebDriver driver) {
    super(driver);
  }

  public ArticleDetailPage navigateTo(String baseUrl, String slug) {
    driver.get(baseUrl + ARTICLE_URL_PREFIX + slug);
    return this;
  }

  public ArticleDetailPage navigateToNonExistent(String baseUrl) {
    driver.get(baseUrl + ARTICLE_URL_PREFIX + "non-existent-article-" + System.currentTimeMillis());
    return this;
  }

  public boolean isOnArticlePage() {
    try {
      return waitForVisibility(articleTitle).isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public String getArticleTitle() {
    return getText(articleTitle);
  }

  public String getDisplayedDate() {
    return getText(articleDate);
  }

  public String getAuthorName() {
    return getText(authorLink);
  }

  public String getArticleBody() {
    return getText(articleContent);
  }

  public List<WebElement> getTags() {
    return tags;
  }

  public int getTagCount() {
    return tags.size();
  }

  public boolean hasCreatedAtTimestamp() {
    try {
      String date = getDisplayedDate();
      return date != null && !date.isEmpty();
    } catch (Exception e) {
      return false;
    }
  }

  public boolean hasUpdatedAtTimestamp() {
    return hasCreatedAtTimestamp();
  }

  public boolean isTimestampInISO8601Format(String timestamp) {
    if (timestamp == null || timestamp.isEmpty()) {
      return false;
    }

    Pattern iso8601Pattern =
        Pattern.compile("^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}(\\.\\d{3})?Z?$");
    Matcher matcher = iso8601Pattern.matcher(timestamp);

    if (matcher.matches()) {
      return true;
    }

    try {
      DateTimeFormatter.ISO_INSTANT.parse(timestamp);
      return true;
    } catch (DateTimeParseException e) {
      try {
        DateTimeFormatter.ISO_DATE_TIME.parse(timestamp);
        return true;
      } catch (DateTimeParseException e2) {
        return false;
      }
    }
  }

  public boolean isValidYear(String timestamp) {
    try {
      Instant instant = Instant.parse(timestamp);
      int year = instant.atZone(java.time.ZoneOffset.UTC).getYear();
      return year >= 2020 && year <= 2030;
    } catch (Exception e) {
      Pattern yearPattern = Pattern.compile("^(\\d{4})-");
      Matcher matcher = yearPattern.matcher(timestamp);
      if (matcher.find()) {
        int year = Integer.parseInt(matcher.group(1));
        return year >= 2020 && year <= 2030;
      }
      return false;
    }
  }

  public boolean isValidMonth(String timestamp) {
    try {
      Instant instant = Instant.parse(timestamp);
      int month = instant.atZone(java.time.ZoneOffset.UTC).getMonthValue();
      return month >= 1 && month <= 12;
    } catch (Exception e) {
      Pattern monthPattern = Pattern.compile("^\\d{4}-(\\d{2})-");
      Matcher matcher = monthPattern.matcher(timestamp);
      if (matcher.find()) {
        int month = Integer.parseInt(matcher.group(1));
        return month >= 1 && month <= 12;
      }
      return false;
    }
  }

  public boolean isValidDay(String timestamp) {
    try {
      Instant instant = Instant.parse(timestamp);
      int day = instant.atZone(java.time.ZoneOffset.UTC).getDayOfMonth();
      return day >= 1 && day <= 31;
    } catch (Exception e) {
      Pattern dayPattern = Pattern.compile("^\\d{4}-\\d{2}-(\\d{2})");
      Matcher matcher = dayPattern.matcher(timestamp);
      if (matcher.find()) {
        int day = Integer.parseInt(matcher.group(1));
        return day >= 1 && day <= 31;
      }
      return false;
    }
  }

  public boolean isValidTime(String timestamp) {
    try {
      Instant instant = Instant.parse(timestamp);
      int hour = instant.atZone(java.time.ZoneOffset.UTC).getHour();
      int minute = instant.atZone(java.time.ZoneOffset.UTC).getMinute();
      int second = instant.atZone(java.time.ZoneOffset.UTC).getSecond();
      return hour >= 0 && hour <= 23 && minute >= 0 && minute <= 59 && second >= 0 && second <= 59;
    } catch (Exception e) {
      Pattern timePattern = Pattern.compile("T(\\d{2}):(\\d{2}):(\\d{2})");
      Matcher matcher = timePattern.matcher(timestamp);
      if (matcher.find()) {
        int hour = Integer.parseInt(matcher.group(1));
        int minute = Integer.parseInt(matcher.group(2));
        int second = Integer.parseInt(matcher.group(3));
        return hour >= 0
            && hour <= 23
            && minute >= 0
            && minute <= 59
            && second >= 0
            && second <= 59;
      }
      return false;
    }
  }

  public boolean hasTimestampPrecision() {
    String date = getDisplayedDate();
    return date != null && date.length() > 10;
  }

  public void clickEditButton() {
    click(editButton);
  }

  public void clickDeleteButton() {
    click(deleteButton);
  }

  public boolean isEditButtonVisible() {
    try {
      return isDisplayed(editButton);
    } catch (Exception e) {
      return false;
    }
  }

  public boolean isDeleteButtonVisible() {
    try {
      return isDisplayed(deleteButton);
    } catch (Exception e) {
      return false;
    }
  }

  public boolean isErrorPageDisplayed() {
    try {
      return driver.getPageSource().contains("404")
          || driver.getPageSource().contains("not found")
          || driver.getPageSource().contains("error");
    } catch (Exception e) {
      return false;
    }
  }

  public void waitForPageLoad() {
    wait.until(ExpectedConditions.visibilityOf(articleMeta));
  }

  public void setCreatedAtTimestamp(String timestamp) {
    this.createdAtTimestamp = timestamp;
  }

  public String getCreatedAtTimestamp() {
    return this.createdAtTimestamp;
  }

  public void setUpdatedAtTimestamp(String timestamp) {
    this.updatedAtTimestamp = timestamp;
  }

  public String getUpdatedAtTimestamp() {
    return this.updatedAtTimestamp;
  }

  public void refresh() {
    driver.navigate().refresh();
  }

  public void navigateBack() {
    driver.navigate().back();
  }
}

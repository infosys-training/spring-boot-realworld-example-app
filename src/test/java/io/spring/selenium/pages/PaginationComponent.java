package io.spring.selenium.pages;

import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class PaginationComponent {

  private WebDriver driver;
  private WebElement paginationElement;
  private WebDriverWait wait;

  private static final By PAGE_ITEMS_LOCATOR = By.cssSelector(".page-item");
  private static final By PAGE_LINK_LOCATOR = By.cssSelector(".page-link");
  private static final By ACTIVE_PAGE_LOCATOR = By.cssSelector(".page-item.active");

  public PaginationComponent(WebDriver driver, WebElement paginationElement) {
    this.driver = driver;
    this.paginationElement = paginationElement;
    this.wait = new WebDriverWait(driver, 10);
  }

  public boolean isDisplayed() {
    try {
      return paginationElement != null && paginationElement.isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public List<WebElement> getPageItems() {
    try {
      return paginationElement.findElements(PAGE_ITEMS_LOCATOR);
    } catch (Exception e) {
      return List.of();
    }
  }

  public int getPageCount() {
    List<WebElement> pageItems = getPageItems();
    int count = 0;
    for (WebElement item : pageItems) {
      String text = item.getText().trim();
      if (text.matches("\\d+")) {
        count++;
      }
    }
    return count;
  }

  public int getCurrentPage() {
    try {
      WebElement activePage = paginationElement.findElement(ACTIVE_PAGE_LOCATOR);
      String text = activePage.getText().trim();
      if (text.matches("\\d+")) {
        return Integer.parseInt(text);
      }
      return 1;
    } catch (Exception e) {
      return 1;
    }
  }

  public boolean isPageActive(int pageNumber) {
    try {
      WebElement activePage = paginationElement.findElement(ACTIVE_PAGE_LOCATOR);
      String text = activePage.getText().trim();
      return text.equals(String.valueOf(pageNumber));
    } catch (Exception e) {
      return false;
    }
  }

  public void clickPage(int pageNumber) {
    List<WebElement> pageItems = getPageItems();
    for (WebElement item : pageItems) {
      String text = item.getText().trim();
      if (text.equals(String.valueOf(pageNumber))) {
        item.click();
        waitForPageChange();
        return;
      }
    }
    throw new RuntimeException("Page number " + pageNumber + " not found in pagination");
  }

  public void clickFirstPage() {
    List<WebElement> pageItems = getPageItems();
    for (WebElement item : pageItems) {
      String text = item.getText().trim();
      if (text.equals("<<")) {
        item.click();
        waitForPageChange();
        return;
      }
    }
    throw new RuntimeException("First page button (<<) not found");
  }

  public void clickLastPage() {
    List<WebElement> pageItems = getPageItems();
    for (WebElement item : pageItems) {
      String text = item.getText().trim();
      if (text.equals(">>")) {
        item.click();
        waitForPageChange();
        return;
      }
    }
    throw new RuntimeException("Last page button (>>) not found");
  }

  public void clickNextPage() {
    List<WebElement> pageItems = getPageItems();
    for (WebElement item : pageItems) {
      String text = item.getText().trim();
      if (text.equals(">")) {
        item.click();
        waitForPageChange();
        return;
      }
    }
    throw new RuntimeException("Next page button (>) not found");
  }

  public void clickPreviousPage() {
    List<WebElement> pageItems = getPageItems();
    for (WebElement item : pageItems) {
      String text = item.getText().trim();
      if (text.equals("<")) {
        item.click();
        waitForPageChange();
        return;
      }
    }
    throw new RuntimeException("Previous page button (<) not found");
  }

  public boolean hasNextPageButton() {
    List<WebElement> pageItems = getPageItems();
    for (WebElement item : pageItems) {
      String text = item.getText().trim();
      if (text.equals(">")) {
        return true;
      }
    }
    return false;
  }

  public boolean hasPreviousPageButton() {
    List<WebElement> pageItems = getPageItems();
    for (WebElement item : pageItems) {
      String text = item.getText().trim();
      if (text.equals("<")) {
        return true;
      }
    }
    return false;
  }

  public boolean hasFirstPageButton() {
    List<WebElement> pageItems = getPageItems();
    for (WebElement item : pageItems) {
      String text = item.getText().trim();
      if (text.equals("<<")) {
        return true;
      }
    }
    return false;
  }

  public boolean hasLastPageButton() {
    List<WebElement> pageItems = getPageItems();
    for (WebElement item : pageItems) {
      String text = item.getText().trim();
      if (text.equals(">>")) {
        return true;
      }
    }
    return false;
  }

  private void waitForPageChange() {
    try {
      Thread.sleep(500);
      wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".article-preview")));
    } catch (Exception e) {
      // Page might still be loading
    }
  }

  public List<Integer> getVisiblePageNumbers() {
    List<WebElement> pageItems = getPageItems();
    return pageItems.stream()
        .map(item -> item.getText().trim())
        .filter(text -> text.matches("\\d+"))
        .map(Integer::parseInt)
        .collect(java.util.stream.Collectors.toList());
  }
}

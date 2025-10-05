package io.spring.application;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PageTest {

  @Test
  public void should_create_with_valid_parameters() {
    Page page = new Page(10, 20);

    Assertions.assertEquals(10, page.getOffset());
    Assertions.assertEquals(20, page.getLimit());
  }

  @Test
  public void should_create_with_default_constructor() {
    Page page = new Page();

    Assertions.assertEquals(0, page.getOffset());
    Assertions.assertEquals(20, page.getLimit());
  }

  @Test
  public void should_enforce_max_limit() {
    Page page = new Page(0, 200);

    Assertions.assertEquals(100, page.getLimit());
  }

  @Test
  public void should_ignore_negative_limit() {
    Page page = new Page(0, -10);

    Assertions.assertEquals(20, page.getLimit());
  }

  @Test
  public void should_ignore_zero_limit() {
    Page page = new Page(0, 0);

    Assertions.assertEquals(20, page.getLimit());
  }

  @Test
  public void should_accept_limit_at_max_boundary() {
    Page page = new Page(0, 100);

    Assertions.assertEquals(100, page.getLimit());
  }

  @Test
  public void should_accept_limit_of_one() {
    Page page = new Page(0, 1);

    Assertions.assertEquals(1, page.getLimit());
  }

  @Test
  public void should_ignore_negative_offset() {
    Page page = new Page(-10, 20);

    Assertions.assertEquals(0, page.getOffset());
  }

  @Test
  public void should_ignore_zero_offset() {
    Page page = new Page(0, 20);

    Assertions.assertEquals(0, page.getOffset());
  }

  @Test
  public void should_accept_positive_offset() {
    Page page = new Page(50, 20);

    Assertions.assertEquals(50, page.getOffset());
  }

  @Test
  public void should_support_equals() {
    Page page1 = new Page(10, 20);
    Page page2 = new Page(10, 20);

    Assertions.assertEquals(page1, page2);
  }

  @Test
  public void should_support_hash_code() {
    Page page1 = new Page(10, 20);
    Page page2 = new Page(10, 20);

    Assertions.assertEquals(page1.hashCode(), page2.hashCode());
  }

  @Test
  public void should_support_to_string() {
    Page page = new Page(10, 20);

    String result = page.toString();

    Assertions.assertNotNull(result);
    Assertions.assertTrue(result.contains("10"));
    Assertions.assertTrue(result.contains("20"));
  }
}

package io.spring.core.article;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TagTest {

  @Test
  public void should_create_tag_with_name() {
    Tag tag = new Tag("java");

    Assertions.assertEquals("java", tag.getName());
    Assertions.assertNotNull(tag.getId());
  }

  @Test
  public void should_generate_unique_ids() {
    Tag tag1 = new Tag("java");
    Tag tag2 = new Tag("java");

    Assertions.assertNotEquals(tag1.getId(), tag2.getId());
  }

  @Test
  public void should_create_with_no_args_constructor() {
    Tag tag = new Tag();

    Assertions.assertNull(tag.getId());
    Assertions.assertNull(tag.getName());
  }

  @Test
  public void should_set_name() {
    Tag tag = new Tag();
    tag.setName("python");

    Assertions.assertEquals("python", tag.getName());
  }

  @Test
  public void should_set_id() {
    Tag tag = new Tag();
    tag.setId("custom-id");

    Assertions.assertEquals("custom-id", tag.getId());
  }

  @Test
  public void should_be_equal_when_names_match() {
    Tag tag1 = new Tag("java");
    Tag tag2 = new Tag("java");

    Assertions.assertEquals(tag1, tag2);
  }

  @Test
  public void should_not_be_equal_when_names_differ() {
    Tag tag1 = new Tag("java");
    Tag tag2 = new Tag("python");

    Assertions.assertNotEquals(tag1, tag2);
  }

  @Test
  public void should_be_equal_when_names_match_regardless_of_id() {
    Tag tag1 = new Tag("java");
    tag1.setId("id-1");
    
    Tag tag2 = new Tag("java");
    tag2.setId("id-2");

    Assertions.assertEquals(tag1, tag2);
  }

  @Test
  public void should_have_same_hash_code_when_names_match() {
    Tag tag1 = new Tag("java");
    Tag tag2 = new Tag("java");

    Assertions.assertEquals(tag1.hashCode(), tag2.hashCode());
  }

  @Test
  public void should_support_to_string() {
    Tag tag = new Tag("java");

    String result = tag.toString();

    Assertions.assertNotNull(result);
    Assertions.assertTrue(result.contains("java"));
  }
}

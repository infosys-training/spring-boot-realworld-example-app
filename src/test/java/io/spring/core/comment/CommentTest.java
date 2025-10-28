package io.spring.core.comment;

import org.joda.time.DateTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CommentTest {

  @Test
  public void should_create_comment_with_body_user_and_article() {
    Comment comment = new Comment("Great article!", "user-123", "article-456");

    Assertions.assertEquals("Great article!", comment.getBody());
    Assertions.assertEquals("user-123", comment.getUserId());
    Assertions.assertEquals("article-456", comment.getArticleId());
    Assertions.assertNotNull(comment.getId());
    Assertions.assertNotNull(comment.getCreatedAt());
  }

  @Test
  public void should_generate_unique_ids() {
    Comment comment1 = new Comment("Comment 1", "user-123", "article-456");
    Comment comment2 = new Comment("Comment 2", "user-123", "article-456");

    Assertions.assertNotEquals(comment1.getId(), comment2.getId());
  }

  @Test
  public void should_set_created_at_to_current_time() {
    DateTime before = new DateTime();
    Comment comment = new Comment("Test", "user-123", "article-456");
    DateTime after = new DateTime();

    Assertions.assertTrue(comment.getCreatedAt().isAfter(before.minusSeconds(1)));
    Assertions.assertTrue(comment.getCreatedAt().isBefore(after.plusSeconds(1)));
  }

  @Test
  public void should_create_with_no_args_constructor() {
    Comment comment = new Comment();

    Assertions.assertNull(comment.getId());
    Assertions.assertNull(comment.getBody());
    Assertions.assertNull(comment.getUserId());
    Assertions.assertNull(comment.getArticleId());
    Assertions.assertNull(comment.getCreatedAt());
  }

  @Test
  public void should_be_equal_when_ids_match() {
    Comment comment1 = new Comment("Comment 1", "user-123", "article-456");
    Comment comment2 = new Comment("Comment 2", "user-789", "article-999");
    
    String sharedId = comment1.getId();
    Comment comment3 = new Comment();
    setId(comment3, sharedId);

    Assertions.assertEquals(comment1, comment3);
  }

  @Test
  public void should_not_be_equal_when_ids_differ() {
    Comment comment1 = new Comment("Comment 1", "user-123", "article-456");
    Comment comment2 = new Comment("Comment 2", "user-123", "article-456");

    Assertions.assertNotEquals(comment1, comment2);
  }

  @Test
  public void should_have_same_hash_code_when_ids_match() {
    Comment comment1 = new Comment("Comment 1", "user-123", "article-456");
    String id = comment1.getId();
    
    Comment comment2 = new Comment();
    setId(comment2, id);

    Assertions.assertEquals(comment1.hashCode(), comment2.hashCode());
  }

  @Test
  public void should_not_be_equal_to_null() {
    Comment comment = new Comment("Test", "user-123", "article-456");

    Assertions.assertNotEquals(comment, null);
  }

  @Test
  public void should_be_equal_to_itself() {
    Comment comment = new Comment("Test", "user-123", "article-456");

    Assertions.assertEquals(comment, comment);
  }

  private void setId(Comment comment, String id) {
    try {
      java.lang.reflect.Field field = Comment.class.getDeclaredField("id");
      field.setAccessible(true);
      field.set(comment, id);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}

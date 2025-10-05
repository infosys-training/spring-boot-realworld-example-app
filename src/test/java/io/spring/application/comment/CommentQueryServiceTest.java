package io.spring.application.comment;

import io.spring.application.CommentQueryService;
import io.spring.application.CursorPageParameter;
import io.spring.application.CursorPager;
import io.spring.application.CursorPager.Direction;
import io.spring.application.data.CommentData;
import io.spring.core.article.Article;
import io.spring.core.article.ArticleRepository;
import io.spring.core.comment.Comment;
import io.spring.core.comment.CommentRepository;
import io.spring.core.user.FollowRelation;
import io.spring.core.user.User;
import io.spring.core.user.UserRepository;
import io.spring.infrastructure.DbTestBase;
import io.spring.infrastructure.repository.MyBatisArticleRepository;
import io.spring.infrastructure.repository.MyBatisCommentRepository;
import io.spring.infrastructure.repository.MyBatisUserRepository;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

@Import({
  MyBatisCommentRepository.class,
  MyBatisUserRepository.class,
  CommentQueryService.class,
  MyBatisArticleRepository.class
})
public class CommentQueryServiceTest extends DbTestBase {
  @Autowired private CommentRepository commentRepository;

  @Autowired private UserRepository userRepository;

  @Autowired private CommentQueryService commentQueryService;

  @Autowired private ArticleRepository articleRepository;

  private User user;

  @BeforeEach
  public void setUp() {
    user = new User("aisensiy@test.com", "aisensiy", "123", "", "");
    userRepository.save(user);
  }

  @Test
  public void should_read_comment_success() {
    Comment comment = new Comment("content", user.getId(), "123");
    commentRepository.save(comment);

    Optional<CommentData> optional = commentQueryService.findById(comment.getId(), user);
    Assertions.assertTrue(optional.isPresent());
    CommentData commentData = optional.get();
    Assertions.assertEquals(commentData.getProfileData().getUsername(), user.getUsername());
  }

  @Test
  public void should_read_comments_of_article() {
    Article article = new Article("title", "desc", "body", Arrays.asList("java"), user.getId());
    articleRepository.save(article);

    User user2 = new User("user2@email.com", "user2", "123", "", "");
    userRepository.save(user2);
    userRepository.saveRelation(new FollowRelation(user.getId(), user2.getId()));

    Comment comment1 = new Comment("content1", user.getId(), article.getId());
    commentRepository.save(comment1);
    Comment comment2 = new Comment("content2", user2.getId(), article.getId());
    commentRepository.save(comment2);

    List<CommentData> comments = commentQueryService.findByArticleId(article.getId(), user);
    Assertions.assertEquals(comments.size(), 2);
  }

  @Test
  public void should_return_empty_when_comment_not_found() {
    Optional<CommentData> optional = commentQueryService.findById("non-existent-id", user);
    Assertions.assertFalse(optional.isPresent());
  }

  @Test
  public void should_return_empty_list_when_no_comments_for_article() {
    Article article = new Article("title", "desc", "body", Arrays.asList("java"), user.getId());
    articleRepository.save(article);

    List<CommentData> comments = commentQueryService.findByArticleId(article.getId(), user);
    Assertions.assertEquals(0, comments.size());
  }

  @Test
  public void should_read_comments_with_null_user() {
    Article article = new Article("title", "desc", "body", Arrays.asList("java"), user.getId());
    articleRepository.save(article);

    Comment comment = new Comment("content", user.getId(), article.getId());
    commentRepository.save(comment);

    List<CommentData> comments = commentQueryService.findByArticleId(article.getId(), null);
    Assertions.assertEquals(1, comments.size());
    Assertions.assertFalse(comments.get(0).getProfileData().isFollowing());
  }

  @Test
  public void should_set_following_flag_when_user_follows_author() {
    Article article = new Article("title", "desc", "body", Arrays.asList("java"), user.getId());
    articleRepository.save(article);

    User author = new User("author@email.com", "author", "123", "", "");
    userRepository.save(author);
    userRepository.saveRelation(new FollowRelation(user.getId(), author.getId()));

    Comment comment = new Comment("content", author.getId(), article.getId());
    commentRepository.save(comment);

    List<CommentData> comments = commentQueryService.findByArticleId(article.getId(), user);
    Assertions.assertEquals(1, comments.size());
    Assertions.assertTrue(comments.get(0).getProfileData().isFollowing());
  }

  @Test
  public void should_read_comments_with_cursor_next_direction() throws InterruptedException {
    Article article = new Article("title", "desc", "body", Arrays.asList("java"), user.getId());
    articleRepository.save(article);

    Comment comment1 = new Comment("content1", user.getId(), article.getId());
    commentRepository.save(comment1);
    Thread.sleep(100);
    Comment comment2 = new Comment("content2", user.getId(), article.getId());
    commentRepository.save(comment2);

    CursorPageParameter<DateTime> page = new CursorPageParameter<>(null, 10, Direction.NEXT);
    CursorPager<CommentData> pager = commentQueryService.findByArticleIdWithCursor(article.getId(), user, page);

    Assertions.assertEquals(2, pager.getData().size());
    Assertions.assertFalse(pager.hasNext());
    Assertions.assertFalse(pager.hasPrevious());
  }

  @Test
  public void should_read_comments_with_cursor_prev_direction() throws InterruptedException {
    Article article = new Article("title", "desc", "body", Arrays.asList("java"), user.getId());
    articleRepository.save(article);

    Comment comment1 = new Comment("content1", user.getId(), article.getId());
    commentRepository.save(comment1);
    Thread.sleep(100);
    Comment comment2 = new Comment("content2", user.getId(), article.getId());
    commentRepository.save(comment2);

    CursorPageParameter<DateTime> page = new CursorPageParameter<>(null, 10, Direction.PREV);
    CursorPager<CommentData> pager = commentQueryService.findByArticleIdWithCursor(article.getId(), user, page);

    Assertions.assertEquals(2, pager.getData().size());
    Assertions.assertFalse(pager.hasNext());
    Assertions.assertFalse(pager.hasPrevious());
  }

  @Test
  public void should_return_empty_pager_when_no_comments_with_cursor() {
    Article article = new Article("title", "desc", "body", Arrays.asList("java"), user.getId());
    articleRepository.save(article);

    CursorPageParameter<DateTime> page = new CursorPageParameter<>(null, 10, Direction.NEXT);
    CursorPager<CommentData> pager = commentQueryService.findByArticleIdWithCursor(article.getId(), user, page);

    Assertions.assertEquals(0, pager.getData().size());
    Assertions.assertFalse(pager.hasNext());
    Assertions.assertFalse(pager.hasPrevious());
  }

  @Test
  public void should_indicate_has_next_when_more_comments_exist() throws InterruptedException {
    Article article = new Article("title", "desc", "body", Arrays.asList("java"), user.getId());
    articleRepository.save(article);

    for (int i = 0; i < 5; i++) {
      Comment comment = new Comment("content" + i, user.getId(), article.getId());
      commentRepository.save(comment);
      Thread.sleep(50);
    }

    CursorPageParameter<DateTime> page = new CursorPageParameter<>(null, 3, Direction.NEXT);
    CursorPager<CommentData> pager = commentQueryService.findByArticleIdWithCursor(article.getId(), user, page);

    Assertions.assertEquals(3, pager.getData().size());
    Assertions.assertTrue(pager.hasNext());
    Assertions.assertFalse(pager.hasPrevious());
  }

  @Test
  public void should_indicate_has_previous_when_more_comments_exist() throws InterruptedException {
    Article article = new Article("title", "desc", "body", Arrays.asList("java"), user.getId());
    articleRepository.save(article);

    for (int i = 0; i < 5; i++) {
      Comment comment = new Comment("content" + i, user.getId(), article.getId());
      commentRepository.save(comment);
      Thread.sleep(50);
    }

    CursorPageParameter<DateTime> page = new CursorPageParameter<>(null, 3, Direction.PREV);
    CursorPager<CommentData> pager = commentQueryService.findByArticleIdWithCursor(article.getId(), user, page);

    Assertions.assertEquals(3, pager.getData().size());
    Assertions.assertFalse(pager.hasNext());
    Assertions.assertTrue(pager.hasPrevious());
  }

  @Test
  public void should_set_following_flag_in_cursor_pagination() throws InterruptedException {
    Article article = new Article("title", "desc", "body", Arrays.asList("java"), user.getId());
    articleRepository.save(article);

    User author = new User("author@email.com", "author", "123", "", "");
    userRepository.save(author);
    userRepository.saveRelation(new FollowRelation(user.getId(), author.getId()));

    Comment comment1 = new Comment("content1", author.getId(), article.getId());
    commentRepository.save(comment1);
    Thread.sleep(100);
    Comment comment2 = new Comment("content2", user.getId(), article.getId());
    commentRepository.save(comment2);

    CursorPageParameter<DateTime> page = new CursorPageParameter<>(null, 10, Direction.NEXT);
    CursorPager<CommentData> pager = commentQueryService.findByArticleIdWithCursor(article.getId(), user, page);

    Assertions.assertEquals(2, pager.getData().size());
    CommentData authorComment = pager.getData().stream()
        .filter(c -> c.getProfileData().getUsername().equals("author"))
        .findFirst()
        .get();
    Assertions.assertTrue(authorComment.getProfileData().isFollowing());
  }

  @Test
  public void should_handle_null_user_in_cursor_pagination() {
    Article article = new Article("title", "desc", "body", Arrays.asList("java"), user.getId());
    articleRepository.save(article);

    Comment comment = new Comment("content", user.getId(), article.getId());
    commentRepository.save(comment);

    CursorPageParameter<DateTime> page = new CursorPageParameter<>(null, 10, Direction.NEXT);
    CursorPager<CommentData> pager = commentQueryService.findByArticleIdWithCursor(article.getId(), null, page);

    Assertions.assertEquals(1, pager.getData().size());
    Assertions.assertFalse(pager.getData().get(0).getProfileData().isFollowing());
  }

  @Test
  public void should_get_start_and_end_cursor() throws InterruptedException {
    Article article = new Article("title", "desc", "body", Arrays.asList("java"), user.getId());
    articleRepository.save(article);

    Comment comment1 = new Comment("content1", user.getId(), article.getId());
    commentRepository.save(comment1);
    Thread.sleep(100);
    Comment comment2 = new Comment("content2", user.getId(), article.getId());
    commentRepository.save(comment2);

    CursorPageParameter<DateTime> page = new CursorPageParameter<>(null, 10, Direction.NEXT);
    CursorPager<CommentData> pager = commentQueryService.findByArticleIdWithCursor(article.getId(), user, page);

    Assertions.assertNotNull(pager.getStartCursor());
    Assertions.assertNotNull(pager.getEndCursor());
  }

}

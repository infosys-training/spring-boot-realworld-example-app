package io.spring.api;

import io.spring.api.exception.ResourceNotFoundException;
import io.spring.application.CommentQueryService;
import io.spring.application.data.CommentData;
import io.spring.core.article.Article;
import io.spring.core.article.ArticleRepository;
import io.spring.core.comment.Comment;
import io.spring.core.comment.CommentReaction;
import io.spring.core.comment.CommentReactionRepository;
import io.spring.core.comment.CommentRepository;
import io.spring.core.user.User;
import java.util.HashMap;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/articles/{slug}/comments/{commentId}")
@AllArgsConstructor
public class CommentReactionApi {
  private CommentReactionRepository reactionRepository;
  private CommentRepository commentRepository;
  private ArticleRepository articleRepository;
  private CommentQueryService commentQueryService;

  @PostMapping(path = "/like")
  public ResponseEntity<HashMap<String, Object>> likeComment(
      @PathVariable("slug") String slug,
      @PathVariable("commentId") String commentId,
      @AuthenticationPrincipal User user) {
    Article article =
        articleRepository.findBySlug(slug).orElseThrow(ResourceNotFoundException::new);
    Comment comment =
        commentRepository
            .findById(article.getId(), commentId)
            .orElseThrow(ResourceNotFoundException::new);
    CommentReaction reaction =
        new CommentReaction(comment.getId(), user.getId(), CommentReaction.ReactionType.LIKE);
    reactionRepository.save(reaction);
    return responseCommentData(commentQueryService.findById(commentId, user).get());
  }

  @DeleteMapping(path = "/like")
  public ResponseEntity deleteCommentLike(
      @PathVariable("slug") String slug,
      @PathVariable("commentId") String commentId,
      @AuthenticationPrincipal User user) {
    Article article =
        articleRepository.findBySlug(slug).orElseThrow(ResourceNotFoundException::new);
    Comment comment =
        commentRepository
            .findById(article.getId(), commentId)
            .orElseThrow(ResourceNotFoundException::new);
    reactionRepository
        .find(comment.getId(), user.getId())
        .ifPresent(
            reaction -> {
              if (reaction.getReactionType() == CommentReaction.ReactionType.LIKE) {
                reactionRepository.remove(reaction);
              }
            });
    return ResponseEntity.noContent().build();
  }

  @PostMapping(path = "/dislike")
  public ResponseEntity<HashMap<String, Object>> dislikeComment(
      @PathVariable("slug") String slug,
      @PathVariable("commentId") String commentId,
      @AuthenticationPrincipal User user) {
    Article article =
        articleRepository.findBySlug(slug).orElseThrow(ResourceNotFoundException::new);
    Comment comment =
        commentRepository
            .findById(article.getId(), commentId)
            .orElseThrow(ResourceNotFoundException::new);
    CommentReaction reaction =
        new CommentReaction(comment.getId(), user.getId(), CommentReaction.ReactionType.DISLIKE);
    reactionRepository.save(reaction);
    return responseCommentData(commentQueryService.findById(commentId, user).get());
  }

  @DeleteMapping(path = "/dislike")
  public ResponseEntity deleteCommentDislike(
      @PathVariable("slug") String slug,
      @PathVariable("commentId") String commentId,
      @AuthenticationPrincipal User user) {
    Article article =
        articleRepository.findBySlug(slug).orElseThrow(ResourceNotFoundException::new);
    Comment comment =
        commentRepository
            .findById(article.getId(), commentId)
            .orElseThrow(ResourceNotFoundException::new);
    reactionRepository
        .find(comment.getId(), user.getId())
        .ifPresent(
            reaction -> {
              if (reaction.getReactionType() == CommentReaction.ReactionType.DISLIKE) {
                reactionRepository.remove(reaction);
              }
            });
    return ResponseEntity.noContent().build();
  }

  private ResponseEntity<HashMap<String, Object>> responseCommentData(
      final CommentData commentData) {
    return ResponseEntity.ok(
        new HashMap<String, Object>() {
          {
            put("comment", commentData);
          }
        });
  }
}

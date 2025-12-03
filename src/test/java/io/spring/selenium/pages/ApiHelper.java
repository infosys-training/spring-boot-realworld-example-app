package io.spring.selenium.pages;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/** Helper class for making direct API calls to test authorization errors. */
public class ApiHelper {

  private final String apiBaseUrl;

  public ApiHelper(String apiBaseUrl) {
    this.apiBaseUrl = apiBaseUrl;
  }

  /** Login and return the JWT token. */
  public String login(String email, String password) throws IOException {
    String jsonBody =
        String.format("{\"user\":{\"email\":\"%s\",\"password\":\"%s\"}}", email, password);
    ApiResponse response = post("/users/login", jsonBody, null);
    if (response.statusCode == 200) {
      int tokenStart = response.body.indexOf("\"token\":\"") + 9;
      int tokenEnd = response.body.indexOf("\"", tokenStart);
      return response.body.substring(tokenStart, tokenEnd);
    }
    return null;
  }

  /** Attempt to edit an article and return the response. */
  public ApiResponse editArticle(
      String slug, String title, String description, String body, String token) throws IOException {
    String jsonBody =
        String.format(
            "{\"article\":{\"title\":\"%s\",\"description\":\"%s\",\"body\":\"%s\"}}",
            title != null ? title : "",
            description != null ? description : "",
            body != null ? body : "");
    return put("/articles/" + slug, jsonBody, token);
  }

  /** Attempt to delete an article and return the response. */
  public ApiResponse deleteArticle(String slug, String token) throws IOException {
    return delete("/articles/" + slug, token);
  }

  /** Attempt to delete a comment and return the response. */
  public ApiResponse deleteComment(String slug, String commentId, String token) throws IOException {
    return delete("/articles/" + slug + "/comments/" + commentId, token);
  }

  /** Get an article by slug. */
  public ApiResponse getArticle(String slug, String token) throws IOException {
    return get("/articles/" + slug, token);
  }

  /** Get comments for an article. */
  public ApiResponse getComments(String slug, String token) throws IOException {
    return get("/articles/" + slug + "/comments", token);
  }

  /** Create a new article. */
  public ApiResponse createArticle(
      String title, String description, String body, String[] tags, String token)
      throws IOException {
    StringBuilder tagsJson = new StringBuilder("[");
    if (tags != null) {
      for (int i = 0; i < tags.length; i++) {
        tagsJson.append("\"").append(tags[i]).append("\"");
        if (i < tags.length - 1) {
          tagsJson.append(",");
        }
      }
    }
    tagsJson.append("]");

    String jsonBody =
        String.format(
            "{\"article\":{\"title\":\"%s\",\"description\":\"%s\",\"body\":\"%s\",\"tagList\":%s}}",
            title, description, body, tagsJson);
    return post("/articles", jsonBody, token);
  }

  /** Add a comment to an article. */
  public ApiResponse addComment(String slug, String commentBody, String token) throws IOException {
    String jsonBody = String.format("{\"comment\":{\"body\":\"%s\"}}", commentBody);
    return post("/articles/" + slug + "/comments", jsonBody, token);
  }

  private ApiResponse get(String endpoint, String token) throws IOException {
    return request("GET", endpoint, null, token);
  }

  private ApiResponse post(String endpoint, String jsonBody, String token) throws IOException {
    return request("POST", endpoint, jsonBody, token);
  }

  private ApiResponse put(String endpoint, String jsonBody, String token) throws IOException {
    return request("PUT", endpoint, jsonBody, token);
  }

  private ApiResponse delete(String endpoint, String token) throws IOException {
    return request("DELETE", endpoint, null, token);
  }

  private ApiResponse request(String method, String endpoint, String jsonBody, String token)
      throws IOException {
    URL url = new URL(apiBaseUrl + endpoint);
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    conn.setRequestMethod(method);
    conn.setRequestProperty("Content-Type", "application/json");

    if (token != null && !token.isEmpty()) {
      conn.setRequestProperty("Authorization", "Token " + token);
    }

    if (jsonBody != null && !jsonBody.isEmpty()) {
      conn.setDoOutput(true);
      try (OutputStream os = conn.getOutputStream()) {
        os.write(jsonBody.getBytes(StandardCharsets.UTF_8));
      }
    }

    int statusCode = conn.getResponseCode();
    String contentType = conn.getContentType();

    StringBuilder response = new StringBuilder();
    try (BufferedReader br =
        new BufferedReader(
            new InputStreamReader(
                statusCode >= 400 ? conn.getErrorStream() : conn.getInputStream(),
                StandardCharsets.UTF_8))) {
      String line;
      while ((line = br.readLine()) != null) {
        response.append(line);
      }
    } catch (Exception e) {
      // Error stream might be null
    }

    return new ApiResponse(statusCode, response.toString(), contentType);
  }

  /** Response wrapper class. */
  public static class ApiResponse {
    public final int statusCode;
    public final String body;
    public final String contentType;

    public ApiResponse(int statusCode, String body, String contentType) {
      this.statusCode = statusCode;
      this.body = body;
      this.contentType = contentType;
    }

    public boolean containsSensitiveInfo() {
      String lowerBody = body.toLowerCase();
      return lowerBody.contains("stacktrace")
          || lowerBody.contains("exception")
          || lowerBody.contains("at io.spring")
          || lowerBody.contains("at java.")
          || lowerBody.contains("at org.")
          || lowerBody.contains("/home/")
          || lowerBody.contains("/usr/")
          || lowerBody.contains("jdbc:")
          || lowerBody.contains("password")
          || body.matches(
              ".*[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}.*"); // UUID pattern
    }

    public boolean containsEmailAddress() {
      return body.matches(".*[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}.*");
    }
  }
}

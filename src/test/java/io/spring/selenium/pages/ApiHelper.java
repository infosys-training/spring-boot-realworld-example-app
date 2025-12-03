package io.spring.selenium.pages;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ApiHelper {

  private final String apiBaseUrl;
  private String authToken;

  public ApiHelper(String apiBaseUrl) {
    this.apiBaseUrl = apiBaseUrl;
  }

  public String login(String email, String password) throws IOException {
    String jsonBody =
        String.format("{\"user\":{\"email\":\"%s\",\"password\":\"%s\"}}", email, password);

    String response = sendPostRequest("/users/login", jsonBody, null);

    Pattern tokenPattern = Pattern.compile("\"token\":\"([^\"]+)\"");
    Matcher matcher = tokenPattern.matcher(response);
    if (matcher.find()) {
      this.authToken = matcher.group(1);
      return this.authToken;
    }
    return null;
  }

  public String createArticle(String title, String description, String body, String[] tags)
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

    String response = sendPostRequest("/articles", jsonBody, authToken);
    return response;
  }

  public String getArticle(String slug) throws IOException {
    return sendGetRequest("/articles/" + slug, authToken);
  }

  public String updateArticle(String slug, String title, String description, String body)
      throws IOException {
    StringBuilder jsonBody = new StringBuilder("{\"article\":{");
    boolean first = true;

    if (title != null) {
      jsonBody.append("\"title\":\"").append(title).append("\"");
      first = false;
    }
    if (description != null) {
      if (!first) jsonBody.append(",");
      jsonBody.append("\"description\":\"").append(description).append("\"");
      first = false;
    }
    if (body != null) {
      if (!first) jsonBody.append(",");
      jsonBody.append("\"body\":\"").append(body).append("\"");
    }
    jsonBody.append("}}");

    return sendPutRequest("/articles/" + slug, jsonBody.toString(), authToken);
  }

  public void deleteArticle(String slug) throws IOException {
    sendDeleteRequest("/articles/" + slug, authToken);
  }

  public String extractSlug(String articleResponse) {
    Pattern slugPattern = Pattern.compile("\"slug\":\"([^\"]+)\"");
    Matcher matcher = slugPattern.matcher(articleResponse);
    if (matcher.find()) {
      return matcher.group(1);
    }
    return null;
  }

  public String extractCreatedAt(String articleResponse) {
    Pattern createdAtPattern = Pattern.compile("\"createdAt\":\"([^\"]+)\"");
    Matcher matcher = createdAtPattern.matcher(articleResponse);
    if (matcher.find()) {
      return matcher.group(1);
    }
    return null;
  }

  public String extractUpdatedAt(String articleResponse) {
    Pattern updatedAtPattern = Pattern.compile("\"updatedAt\":\"([^\"]+)\"");
    Matcher matcher = updatedAtPattern.matcher(articleResponse);
    if (matcher.find()) {
      return matcher.group(1);
    }
    return null;
  }

  private String sendPostRequest(String endpoint, String jsonBody, String token)
      throws IOException {
    URL url = new URL(apiBaseUrl + endpoint);
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    conn.setRequestMethod("POST");
    conn.setRequestProperty("Content-Type", "application/json");
    if (token != null) {
      conn.setRequestProperty("Authorization", "Token " + token);
    }
    conn.setDoOutput(true);

    try (OutputStream os = conn.getOutputStream()) {
      os.write(jsonBody.getBytes(StandardCharsets.UTF_8));
    }

    return readResponse(conn);
  }

  private String sendGetRequest(String endpoint, String token) throws IOException {
    URL url = new URL(apiBaseUrl + endpoint);
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    conn.setRequestMethod("GET");
    conn.setRequestProperty("Content-Type", "application/json");
    if (token != null) {
      conn.setRequestProperty("Authorization", "Token " + token);
    }

    return readResponse(conn);
  }

  private String sendPutRequest(String endpoint, String jsonBody, String token) throws IOException {
    URL url = new URL(apiBaseUrl + endpoint);
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    conn.setRequestMethod("PUT");
    conn.setRequestProperty("Content-Type", "application/json");
    if (token != null) {
      conn.setRequestProperty("Authorization", "Token " + token);
    }
    conn.setDoOutput(true);

    try (OutputStream os = conn.getOutputStream()) {
      os.write(jsonBody.getBytes(StandardCharsets.UTF_8));
    }

    return readResponse(conn);
  }

  private void sendDeleteRequest(String endpoint, String token) throws IOException {
    URL url = new URL(apiBaseUrl + endpoint);
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    conn.setRequestMethod("DELETE");
    if (token != null) {
      conn.setRequestProperty("Authorization", "Token " + token);
    }
    conn.getResponseCode();
  }

  private String readResponse(HttpURLConnection conn) throws IOException {
    StringBuilder response = new StringBuilder();
    try (BufferedReader br =
        new BufferedReader(
            new InputStreamReader(
                conn.getResponseCode() >= 400 ? conn.getErrorStream() : conn.getInputStream(),
                StandardCharsets.UTF_8))) {
      String line;
      while ((line = br.readLine()) != null) {
        response.append(line);
      }
    }
    return response.toString();
  }

  public String getAuthToken() {
    return authToken;
  }

  public void setAuthToken(String token) {
    this.authToken = token;
  }
}

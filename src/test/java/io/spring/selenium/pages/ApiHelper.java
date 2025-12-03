package io.spring.selenium.pages;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class ApiHelper {

  private final String apiUrl;
  private String authToken;

  public ApiHelper(String apiUrl) {
    this.apiUrl = apiUrl;
  }

  public void setAuthToken(String token) {
    this.authToken = token;
  }

  public String getAuthToken() {
    return authToken;
  }

  public ApiResponse login(String email, String password) {
    String body =
        String.format("{\"user\":{\"email\":\"%s\",\"password\":\"%s\"}}", email, password);
    ApiResponse response = post("/users/login", body, false);
    if (response.getStatusCode() == 200) {
      String responseBody = response.getBody();
      int tokenStart = responseBody.indexOf("\"token\":\"") + 9;
      int tokenEnd = responseBody.indexOf("\"", tokenStart);
      if (tokenStart > 8 && tokenEnd > tokenStart) {
        this.authToken = responseBody.substring(tokenStart, tokenEnd);
      }
    }
    return response;
  }

  public ApiResponse favoriteArticle(String slug) {
    return post("/articles/" + slug + "/favorite", "", true);
  }

  public ApiResponse unfavoriteArticle(String slug) {
    return delete("/articles/" + slug + "/favorite", true);
  }

  public ApiResponse unfavoriteArticleWithoutAuth(String slug) {
    return delete("/articles/" + slug + "/favorite", false);
  }

  public ApiResponse unfavoriteArticleWithToken(String slug, String token) {
    return deleteWithToken("/articles/" + slug + "/favorite", token);
  }

  public ApiResponse getArticle(String slug) {
    return get("/articles/" + slug, false);
  }

  public ApiResponse getArticleWithAuth(String slug) {
    return get("/articles/" + slug, true);
  }

  public ApiResponse post(String endpoint, String body, boolean authenticated) {
    return request("POST", endpoint, body, authenticated, null);
  }

  public ApiResponse get(String endpoint, boolean authenticated) {
    return request("GET", endpoint, null, authenticated, null);
  }

  public ApiResponse delete(String endpoint, boolean authenticated) {
    return request("DELETE", endpoint, null, authenticated, null);
  }

  public ApiResponse deleteWithToken(String endpoint, String token) {
    return request("DELETE", endpoint, null, false, token);
  }

  private ApiResponse request(
      String method, String endpoint, String body, boolean authenticated, String customToken) {
    try {
      URL url = new URL(apiUrl + "/api" + endpoint);
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setRequestMethod(method);
      conn.setRequestProperty("Content-Type", "application/json");

      if (authenticated && authToken != null) {
        conn.setRequestProperty("Authorization", "Token " + authToken);
      } else if (customToken != null) {
        conn.setRequestProperty("Authorization", "Token " + customToken);
      }

      if (body != null && !body.isEmpty()) {
        conn.setDoOutput(true);
        try (OutputStream os = conn.getOutputStream()) {
          os.write(body.getBytes(StandardCharsets.UTF_8));
        }
      }

      int statusCode = conn.getResponseCode();
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

      return new ApiResponse(statusCode, response.toString());
    } catch (Exception e) {
      return new ApiResponse(-1, e.getMessage());
    }
  }

  public static class ApiResponse {
    private final int statusCode;
    private final String body;

    public ApiResponse(int statusCode, String body) {
      this.statusCode = statusCode;
      this.body = body;
    }

    public int getStatusCode() {
      return statusCode;
    }

    public String getBody() {
      return body;
    }

    public boolean isSuccess() {
      return statusCode >= 200 && statusCode < 300;
    }

    public boolean isFavorited() {
      return body.contains("\"favorited\":true");
    }

    public boolean isNotFavorited() {
      return body.contains("\"favorited\":false");
    }

    public int getFavoritesCount() {
      try {
        int start = body.indexOf("\"favoritesCount\":") + 17;
        int end = body.indexOf(",", start);
        if (end == -1) {
          end = body.indexOf("}", start);
        }
        return Integer.parseInt(body.substring(start, end).trim());
      } catch (Exception e) {
        return -1;
      }
    }
  }
}

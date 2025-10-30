package io.spring.application.export;

import io.spring.application.data.ArticleData;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import lombok.NoArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.springframework.stereotype.Service;

@Service
@NoArgsConstructor
public class ArticleCsvExportService {
  private static final DateTimeFormatter DATE_FORMATTER = ISODateTimeFormat.dateTime();
  private static final String[] HEADERS = {
    "Title",
    "Slug",
    "Description",
    "Body",
    "Author",
    "Tags",
    "Created At",
    "Updated At",
    "Favorites Count"
  };

  public byte[] generateCsv(List<ArticleData> articles) throws IOException {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    try (CSVPrinter printer =
        new CSVPrinter(
            new OutputStreamWriter(out, StandardCharsets.UTF_8),
            CSVFormat.DEFAULT.withHeader(HEADERS))) {

      for (ArticleData article : articles) {
        printer.printRecord(
            article.getTitle(),
            article.getSlug(),
            article.getDescription(),
            article.getBody(),
            article.getProfileData().getUsername(),
            String.join("; ", article.getTagList()),
            article.getCreatedAt() != null ? DATE_FORMATTER.print(article.getCreatedAt()) : "",
            article.getUpdatedAt() != null ? DATE_FORMATTER.print(article.getUpdatedAt()) : "",
            article.getFavoritesCount());
      }
    }
    return out.toByteArray();
  }
}

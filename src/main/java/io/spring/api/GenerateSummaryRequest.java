package io.spring.api;

import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Data;

@Data
@JsonRootName("request")
public class GenerateSummaryRequest {
  private String body;
}

package io.spring.application.flight;

import com.fasterxml.jackson.annotation.JsonRootName;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@JsonRootName("booking")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewBookingParam {
  @NotBlank(message = "can't be empty")
  private String flightId;

  @NotBlank(message = "can't be empty")
  private String passengerName;

  @NotBlank(message = "can't be empty")
  @Email(message = "must be a valid email")
  private String passengerEmail;

  private String passengerPhone;

  private String seatNumber;
}

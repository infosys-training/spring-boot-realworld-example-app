package io.spring.core.booking;

import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;

@Getter
@NoArgsConstructor
@EqualsAndHashCode(of = {"id"})
public class Booking {
  private String id;
  private String userId;
  private String flightId;
  private String passengerName;
  private String passengerEmail;
  private String passengerPhone;
  private String seatNumber;
  private String status;
  private DateTime createdAt;
  private DateTime updatedAt;

  public static final String STATUS_CONFIRMED = "CONFIRMED";
  public static final String STATUS_CANCELLED = "CANCELLED";

  public Booking(
      String userId,
      String flightId,
      String passengerName,
      String passengerEmail,
      String passengerPhone,
      String seatNumber) {
    this.id = UUID.randomUUID().toString();
    this.userId = userId;
    this.flightId = flightId;
    this.passengerName = passengerName;
    this.passengerEmail = passengerEmail;
    this.passengerPhone = passengerPhone;
    this.seatNumber = seatNumber;
    this.status = STATUS_CONFIRMED;
    this.createdAt = new DateTime();
    this.updatedAt = new DateTime();
  }

  public void cancel() {
    this.status = STATUS_CANCELLED;
    this.updatedAt = new DateTime();
  }
}

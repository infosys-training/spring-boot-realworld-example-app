package io.spring.core.flight;

import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;

@Getter
@NoArgsConstructor
@EqualsAndHashCode(of = {"id"})
public class Flight {
  private String id;
  private String flightNumber;
  private String airline;
  private String origin;
  private String destination;
  private DateTime departureTime;
  private DateTime arrivalTime;
  private double price;
  private int availableSeats;
  private DateTime createdAt;
  private DateTime updatedAt;

  public Flight(
      String flightNumber,
      String airline,
      String origin,
      String destination,
      DateTime departureTime,
      DateTime arrivalTime,
      double price,
      int availableSeats) {
    this.id = UUID.randomUUID().toString();
    this.flightNumber = flightNumber;
    this.airline = airline;
    this.origin = origin;
    this.destination = destination;
    this.departureTime = departureTime;
    this.arrivalTime = arrivalTime;
    this.price = price;
    this.availableSeats = availableSeats;
    this.createdAt = new DateTime();
    this.updatedAt = new DateTime();
  }

  public void decrementSeats() {
    if (this.availableSeats <= 0) {
      throw new IllegalStateException("No available seats on this flight");
    }
    this.availableSeats--;
    this.updatedAt = new DateTime();
  }

  public void incrementSeats() {
    this.availableSeats++;
    this.updatedAt = new DateTime();
  }
}

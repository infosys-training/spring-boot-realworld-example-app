package io.spring.application.flight;

import io.spring.api.exception.ResourceNotFoundException;
import io.spring.core.booking.Booking;
import io.spring.core.booking.BookingRepository;
import io.spring.core.flight.Flight;
import io.spring.core.flight.FlightRepository;
import io.spring.core.user.User;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@AllArgsConstructor
public class FlightBookingCommandService {

  private FlightRepository flightRepository;
  private BookingRepository bookingRepository;

  @Transactional
  public Booking createBooking(@Valid NewBookingParam param, User user) {
    Flight flight =
        flightRepository
            .findById(param.getFlightId())
            .orElseThrow(() -> new ResourceNotFoundException());

    flight.decrementSeats();
    flightRepository.save(flight);

    Booking booking =
        new Booking(
            user.getId(),
            flight.getId(),
            param.getPassengerName(),
            param.getPassengerEmail(),
            param.getPassengerPhone(),
            param.getSeatNumber());
    bookingRepository.save(booking);
    return booking;
  }

  @Transactional
  public void cancelBooking(String bookingId, User user) {
    Booking booking =
        bookingRepository.findById(bookingId).orElseThrow(() -> new ResourceNotFoundException());

    if (!booking.getUserId().equals(user.getId())) {
      throw new io.spring.api.exception.NoAuthorizationException();
    }

    if (Booking.STATUS_CANCELLED.equals(booking.getStatus())) {
      throw new IllegalStateException("Booking is already cancelled");
    }

    booking.cancel();
    bookingRepository.update(booking);

    flightRepository
        .findById(booking.getFlightId())
        .ifPresent(
            flight -> {
              flight.incrementSeats();
              flightRepository.save(flight);
            });
  }
}

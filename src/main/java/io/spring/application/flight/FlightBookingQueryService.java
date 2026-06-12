package io.spring.application.flight;

import io.spring.core.booking.Booking;
import io.spring.core.booking.BookingRepository;
import io.spring.core.flight.Flight;
import io.spring.core.flight.FlightRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class FlightBookingQueryService {

  private FlightRepository flightRepository;
  private BookingRepository bookingRepository;

  public Optional<Flight> findFlightById(String id) {
    return flightRepository.findById(id);
  }

  public Map<String, Object> findFlights(String origin, String destination, int offset, int limit) {
    List<Flight> flights = flightRepository.findAll(origin, destination, offset, limit);
    int count = flightRepository.count(origin, destination);
    Map<String, Object> result = new HashMap<>();
    result.put("flights", flights);
    result.put("flightsCount", count);
    return result;
  }

  public Optional<Booking> findBookingById(String id) {
    return bookingRepository.findById(id);
  }

  public Map<String, Object> findBookingsByUserId(String userId, int offset, int limit) {
    List<Booking> bookings = bookingRepository.findByUserId(userId, offset, limit);
    int count = bookingRepository.countByUserId(userId);
    Map<String, Object> result = new HashMap<>();
    result.put("bookings", bookings);
    result.put("bookingsCount", count);
    return result;
  }
}

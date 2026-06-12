package io.spring.api;

import io.spring.api.exception.ResourceNotFoundException;
import io.spring.application.flight.FlightBookingCommandService;
import io.spring.application.flight.FlightBookingQueryService;
import io.spring.application.flight.NewBookingParam;
import io.spring.core.booking.Booking;
import io.spring.core.user.User;
import java.util.HashMap;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/bookings")
@AllArgsConstructor
public class BookingsApi {

  private FlightBookingCommandService commandService;
  private FlightBookingQueryService queryService;

  @PostMapping
  public ResponseEntity createBooking(
      @Valid @RequestBody NewBookingParam newBookingParam, @AuthenticationPrincipal User user) {
    Booking booking = commandService.createBooking(newBookingParam, user);
    return ResponseEntity.ok(
        new HashMap<String, Object>() {
          {
            put("booking", booking);
          }
        });
  }

  @GetMapping
  public ResponseEntity getBookings(
      @RequestParam(value = "offset", defaultValue = "0") int offset,
      @RequestParam(value = "limit", defaultValue = "20") int limit,
      @AuthenticationPrincipal User user) {
    return ResponseEntity.ok(queryService.findBookingsByUserId(user.getId(), offset, limit));
  }

  @GetMapping(path = "/{id}")
  public ResponseEntity getBookingById(
      @PathVariable("id") String id, @AuthenticationPrincipal User user) {
    Booking booking =
        queryService.findBookingById(id).orElseThrow(() -> new ResourceNotFoundException());
    return ResponseEntity.ok(
        new HashMap<String, Object>() {
          {
            put("booking", booking);
          }
        });
  }

  @DeleteMapping(path = "/{id}")
  public ResponseEntity cancelBooking(
      @PathVariable("id") String id, @AuthenticationPrincipal User user) {
    commandService.cancelBooking(id, user);
    return ResponseEntity.noContent().build();
  }
}

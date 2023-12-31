package pl.matcodem.reservationservice.presentation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.matcodem.reservationservice.application.request.ReservationRequest;
import pl.matcodem.reservationservice.application.request.UpdateReservationRequest;
import pl.matcodem.reservationservice.application.response.ReservationResponse;
import pl.matcodem.reservationservice.domain.model.valueobjects.FlightReservationStatus;
import pl.matcodem.reservationservice.domain.model.valueobjects.ReservationDate;
import pl.matcodem.reservationservice.domain.model.valueobjects.ReservationId;
import pl.matcodem.reservationservice.domain.service.ReservationService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    public ResponseEntity<ReservationResponse> createReservation(@Valid @RequestBody ReservationRequest request) {
        ReservationResponse response = reservationService.createReservation(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping
    public ResponseEntity<ReservationResponse> updateReservation(@Valid @RequestBody UpdateReservationRequest request) {
        ReservationResponse response = reservationService.updateReservation(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{reservationId}")
    public ResponseEntity<ReservationResponse> getReservationById(@PathVariable ReservationId reservationId) {
        return reservationService.getReservationResponseById(reservationId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<Page<ReservationResponse>> getAllReservations(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        Page<ReservationResponse> responses = reservationService.getAllReservations(page, size);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/status")
    public ResponseEntity<List<ReservationResponse>> getReservationsByStatusAndDate(
            @RequestParam FlightReservationStatus status, @RequestParam ReservationDate date) {
        List<ReservationResponse> responses = reservationService.getReservationsByStatusAndDate(status, date);
        return ResponseEntity.ok(responses);
    }

    @DeleteMapping("/cancel/{reservationId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelReservation(@PathVariable ReservationId reservationId) {
        reservationService.cancelReservation(reservationId);
    }

    @DeleteMapping("/{reservationId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteReservation(@PathVariable ReservationId reservationId) {
        reservationService.deleteReservation(reservationId);
    }
}

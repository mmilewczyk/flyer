package pl.matcodem.reservationservice.domain.service;

import pl.matcodem.reservationservice.application.request.ReservationRequest;
import pl.matcodem.reservationservice.application.request.UpdateReservationRequest;
import pl.matcodem.reservationservice.domain.model.Reservation;
import pl.matcodem.reservationservice.domain.model.valueobjects.ReservationId;

import java.util.List;
import java.util.Optional;

public interface ReservationService {
    Optional<Reservation> getReservationById(ReservationId reservationId);

    List<Reservation> getAllReservations();

    Reservation createReservation(ReservationRequest request);

    Reservation updateReservation(UpdateReservationRequest request);

    void deleteReservation(ReservationId reservationId);

    void cancelReservation(ReservationId reservationId);
}



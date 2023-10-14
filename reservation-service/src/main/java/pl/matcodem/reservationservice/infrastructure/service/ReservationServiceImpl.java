package pl.matcodem.reservationservice.infrastructure.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.matcodem.reservationservice.application.request.ReservationRequest;
import pl.matcodem.reservationservice.application.request.UpdateReservationRequest;
import pl.matcodem.reservationservice.application.response.ReservationResponse;
import pl.matcodem.reservationservice.domain.model.Reservation;
import pl.matcodem.reservationservice.domain.model.valueobjects.*;
import pl.matcodem.reservationservice.domain.repository.ReservationRepository;
import pl.matcodem.reservationservice.domain.service.ReservationService;
import pl.matcodem.reservationservice.exceptions.ReservationNotFoundException;
import pl.matcodem.reservationservice.infrastructure.validator.DeletableStatusValidator;
import pl.matcodem.reservationservice.infrastructure.validator.ModificationPeriodValidator;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    public static final String RESERVATION_NOT_FOUND = "Reservation not found";

    private final ReservationRepository repository;
    private final ReservationServiceHelper helper;
    private final DeletableStatusValidator deletableStatusValidator;
    private final ModificationPeriodValidator modificationPeriodValidator;

    @Override
    public ReservationResponse createReservation(ReservationRequest request) {
        FlightNumber flightNumber = request.getFlightNumber();
        helper.ensureFlightExists(flightNumber);

        ReservationCode reservationCode = ReservationCode.of(UUID.randomUUID().toString());
        ReservationDate reservationDate = request.getReservationDate();
        Passenger passenger = request.getPassenger();

        Reservation reservation = new Reservation(
                ReservationId.generate(),
                reservationCode,
                reservationDate,
                passenger,
                flightNumber,
                FlightReservationStatus.PENDING
        );
        repository.save(reservation);

        ReservationResponse.FlightInfo flightInfo = helper.getFlightInfo(flightNumber);
        return helper.buildReservationResponse(reservationDate, passenger, flightInfo);
    }

    @Override
    public ReservationResponse updateReservation(UpdateReservationRequest request) {
        ReservationId reservationId = request.reservationId();
        Passenger newPassenger = request.newPassenger();

        Reservation existingReservation = repository.findById(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException(RESERVATION_NOT_FOUND));

        modificationPeriodValidator.isReservationWithinAllowedModificationPeriod(existingReservation);

        existingReservation.setPassenger(newPassenger);

        Reservation updatedReservation = repository.save(existingReservation);

        FlightNumber flightNumber = updatedReservation.getFlightNumber();
        ReservationResponse.FlightInfo flightInfo = helper.getFlightInfo(flightNumber);

        ReservationDate reservationDate = updatedReservation.getReservationDate();
        return helper.buildReservationResponse(reservationDate, newPassenger, flightInfo);
    }


    @Override
    public Optional<ReservationResponse> getReservationById(ReservationId reservationId) {
        return repository.findById(reservationId)
                .map(reservation -> helper.buildReservationResponse(
                        reservation.getReservationDate(),
                        reservation.getPassenger(),
                        helper.getFlightInfo(reservation.getFlightNumber())
                ));
    }

    @Override
    public List<ReservationResponse> getAllReservations() {
        List<Reservation> allReservations = repository.findAll();

        return allReservations.stream()
                .map(reservation -> helper.buildReservationResponse(
                        reservation.getReservationDate(),
                        reservation.getPassenger(),
                        helper.getFlightInfo(reservation.getFlightNumber())
                ))
                .toList();
    }


    @Override
    public void cancelReservation(ReservationId reservationId) {
        Optional<Reservation> reservationOptional = repository.findById(reservationId);
        if (reservationOptional.isPresent()) {
            Reservation reservation = reservationOptional.get();
            reservation.setStatus(FlightReservationStatus.CANCELED);
            repository.save(reservation);
        } else {
            throw new ReservationNotFoundException(RESERVATION_NOT_FOUND);
        }
    }

    @Override
    public void deleteReservation(ReservationId reservationId) {
        Reservation reservation = repository.findById(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException(RESERVATION_NOT_FOUND));

        deletableStatusValidator.validateDeletableStatus(reservation.getStatus());

        repository.delete(reservation);
    }
}


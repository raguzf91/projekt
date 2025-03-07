package hr.fina.student.projekt.dao;

import java.util.List;

import hr.fina.student.projekt.entity.Reservation;

public interface ReservationDao {

    List<Reservation> findReservationsByUserId(Integer id);
    List<Reservation> deleteReservation(Integer reservationId, Integer userId);
    Boolean findReservation(Integer listingId, Integer userId);
    List<Reservation> findReservationsByListingId(Integer listingId);
} 
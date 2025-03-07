package hr.fina.student.projekt.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import hr.fina.student.projekt.dao.ListingDao;
import hr.fina.student.projekt.dao.ReservationDao;
import hr.fina.student.projekt.entity.Listing;
import hr.fina.student.projekt.entity.Reservation;
import hr.fina.student.projekt.exceptions.database.DatabaseException;
import hr.fina.student.projekt.mapper.ReservationRowMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Repository
public class ReservationDaoImpl implements ReservationDao {

    private final ListingDao listingsDao;
    private final NamedParameterJdbcTemplate jdbcTemplate;
    @Override
    public List<Reservation> findReservationsByUserId(Integer id) {
        log.info("Finding reservations by user id");
        try {
            final String FIND_RESERVATIONS_BY_USER_ID = """
                    SELECT * FROM reservations WHERE user_id = :id AND valid = true
                    """;
            List<Reservation> reservations = jdbcTemplate.query(FIND_RESERVATIONS_BY_USER_ID, Map.of("id", id), new ReservationRowMapper());
            for(Reservation reservation : reservations) {
                Listing listing = listingsDao.findListing(reservation.getListingId());
                reservation.setListing(listing);
            }
            return reservations; 

        } catch(DatabaseException e) {
            log.error("Error while finding reservations by user id");
            throw new DatabaseException("Error while finding reservations by user id");
        }
         
        catch (Exception e) {
            log.error("Error while finding reservations by user id");
            throw new RuntimeException("Error while finding reservations by user id");
        } 

    }

    @Override
    public Boolean findReservation(Integer listingId, Integer userId) {
        log.info("Finding reservation");
        try {
            final String FIND_RESERVATION = """
                    SELECT * FROM Reservations WHERE listing_id=:listingId AND user_id=:userId
                    """;
            Reservation reservation = jdbcTemplate.queryForObject(FIND_RESERVATION, Map.of("listingId", listingId, "userId", userId), new ReservationRowMapper());
            Boolean canceled = reservation.getCanceled();
            Boolean valid = reservation.getValid();
            if(canceled == false && valid == true ) {
                return true;
            } else {
                return false;
            }


        } catch(DatabaseException e) {
            log.error("Error while finding reservation");
            throw new DatabaseException("Error while finding reservation");
        }
        catch (Exception e) {
            log.error("Error while finding reservation");
            throw new RuntimeException("Error while finding reservation");
        }
    }


    @Override
    public List<Reservation> deleteReservation(Integer reservationId, Integer userId) {
        log.info("Deleting reservation");
        try {
            final String DELETE_RESERVATION = """
                    UPDATE Reservations SET canceled = true WHERE id=:reservationId AND user_id=:userId
                    """;
            int rowsAffected = jdbcTemplate.update(DELETE_RESERVATION, Map.of("reservationId", reservationId, "userId", userId));
            List<Reservation> reservations = findReservationsByUserId(userId);
            return reservations;
        } catch(DatabaseException e) {
            log.error("Error while deleting reservation");
            throw new DatabaseException("Error while deleting reservation");
        }
        catch (Exception e) {
            log.error("Error while deleting reservation");
            throw new RuntimeException("Error while deleting reservation");
        }
    }

    @Override
    public List<Reservation> findReservationsByListingId(Integer listingId) {
        log.info("Finding reservations by listing id");
        try {
            final String FIND_RESERVATIONS_BY_LISTING_ID = """
                    SELECT * FROM reservations WHERE listing_id = :listingId AND valid = true
                    """;
            List<Reservation> reservations = jdbcTemplate.query(FIND_RESERVATIONS_BY_LISTING_ID, Map.of("listingId", listingId), new ReservationRowMapper());
            return reservations;
        } catch(DatabaseException e) {
            log.error("Error while finding reservations by listing id");
            throw new DatabaseException("Error while finding reservations by listing id");
        }
        catch (Exception e) {
            log.error("Error while finding reservations by listing id");
            throw new RuntimeException("Error while finding reservations by listing id");
        }
    }

    

    
    
    
}

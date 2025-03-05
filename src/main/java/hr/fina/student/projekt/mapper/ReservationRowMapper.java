package hr.fina.student.projekt.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import hr.fina.student.projekt.entity.Reservation;
public class ReservationRowMapper implements RowMapper<Reservation> {

    @Override
    public Reservation mapRow(ResultSet rs, int rowNum) throws SQLException {
        Reservation reservation = new Reservation();
        reservation.setId(rs.getInt("id"));
        reservation.setReservedFrom(rs.getTimestamp("reserved_from").toLocalDateTime());
        reservation.setReservedUntil(rs.getTimestamp("reserved_until").toLocalDateTime());
        reservation.setNumberOfGuests(rs.getInt("number_of_guests"));
        reservation.setPaymentAmount(rs.getDouble("payment_amount"));
        reservation.setListingId(rs.getInt("listing_id"));
        reservation.setUserId(rs.getInt("user_id"));
        reservation.setCanceled(rs.getBoolean("canceled"));
        return reservation;
    }
    
}

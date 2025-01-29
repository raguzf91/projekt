package hr.fina.student.projekt.mapper;


import hr.fina.student.projekt.entity.Listing;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ListingRowMapper implements RowMapper<Listing> { ;
    @Override
    public Listing mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Listing.builder()
                .id(rs.getInt("id"))
                .title(rs.getString("title"))
                .description(rs.getString("description"))
                .price(rs.getDouble("price"))
                .rating(rs.getDouble("rating"))
                .refundable(rs.getBoolean("refundable"))
                .numberOfBedrooms(rs.getInt("number_of_bedrooms"))
                .numberOfBeds(rs.getInt("number_of_beds"))
                .maxGuests(rs.getInt("maximum_guests"))
                .numberOfReviews(0)
                .typeOfListing(rs.getString("type_of_listing"))
                .numberOfBathrooms(rs.getInt("number_of_bathrooms"))
                .cleaningFee(rs.getDouble("cleaning_fee"))
                .user(null)
                .location(null)
                .photos(null)
                .build();
    }
}

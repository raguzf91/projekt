package hr.fina.student.projekt.mapper;

import hr.fina.student.projekt.entity.Listing;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ListingSecondRowMapper implements RowMapper<Listing> {
    @Override
    public Listing mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Listing.builder()
                .id(rs.getInt("id"))
                .description(rs.getString("description"))
                .title(rs.getString("title"))
                .rating(rs.getDouble("rating"))
                .price(rs.getDouble("price"))
                .user(null)
                .location(null)
                .photos(null)
                .build();
    }
}

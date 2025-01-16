package hr.fina.student.projekt.mapper;

import hr.fina.student.projekt.entity.Photo;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PhotoRowMapper implements RowMapper<Photo> {
    @Override
    public Photo mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Photo.builder()
                .id(rs.getInt("id"))
                .photoUrl(rs.getString("photo_url"))
                .listingId(rs.getInt("listing_id"))
                .build();
    }
}

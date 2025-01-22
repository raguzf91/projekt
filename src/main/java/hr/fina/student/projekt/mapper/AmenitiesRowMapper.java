package hr.fina.student.projekt.mapper;

import hr.fina.student.projekt.entity.Amenity;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AmenitiesRowMapper implements RowMapper<Amenity> {
    @Override
    public Amenity mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Amenity.builder()
                .id(rs.getInt("id"))
                .description(rs.getString("description"))
                .icon(rs.getString("icon"))
                .build();
    }
}

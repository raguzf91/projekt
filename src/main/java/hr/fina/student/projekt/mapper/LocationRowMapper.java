package hr.fina.student.projekt.mapper;

import hr.fina.student.projekt.entity.Location;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class LocationRowMapper implements RowMapper<Location> {
    @Override
    public Location mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Location.builder()
                .id(rs.getInt("id"))
                .address(rs.getString("address"))
                .city(rs.getString("city"))
                .zipcode(rs.getString("zipcode"))
                .country(rs.getString("country"))
                .latitude(rs.getDouble("latitude"))
                .longitude(rs.getDouble("longitude"))
                .build();
    }
}

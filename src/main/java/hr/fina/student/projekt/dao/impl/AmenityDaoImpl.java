package hr.fina.student.projekt.dao.impl;

import hr.fina.student.projekt.entity.Amenity;
import hr.fina.student.projekt.dao.AmenityDao;
import hr.fina.student.projekt.exceptions.database.DatabaseException;
import hr.fina.student.projekt.mapper.AmenitiesRowMapper;
import hr.fina.student.projekt.mapper.LocationRowMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
@Slf4j
public class AmenityDaoImpl implements AmenityDao {
    private final NamedParameterJdbcTemplate jdbc;
    @Override
    public List<Amenity> findAllAmenities() {
        final String FIND_ALL_AMENITIES = "SELECT * FROM amenities";


        log.info("Fetching all amenities from the database");
        try {
            return jdbc.query(FIND_ALL_AMENITIES, new AmenitiesRowMapper());

        } catch (Exception e) {
            log.error("Error fetching amenities from the database");
            log.error(e.getCause().toString());
            throw new DatabaseException("An error has occurred in fetching Amenities from the database");
        }
    }
}

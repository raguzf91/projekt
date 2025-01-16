package hr.fina.student.projekt.dao.impl;

import hr.fina.student.projekt.dao.LocationDao;
import hr.fina.student.projekt.entity.Location;
import hr.fina.student.projekt.exceptions.database.DatabaseException;
import hr.fina.student.projekt.mapper.LocationRowMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.Map;

@Repository
@RequiredArgsConstructor
@Slf4j
public class LocationDaoImpl implements LocationDao {
    private final NamedParameterJdbcTemplate jdbc;

    @Override
    public Location findLocationByListingId(int listingId ) {
        final String FIND_LOCATION_BY_ID = """
                SELECT * FROM locations WHERE listing_id = :id
                """;
        log.info("Fetching location for listing: {}", listingId);
        try {
            return jdbc.queryForObject(FIND_LOCATION_BY_ID, Map.of("id", listingId), new LocationRowMapper());

        } catch (Exception e) {
            log.error("Error fetching location for listing: {}, cause: {}", listingId, e.getCause());
            throw new DatabaseException("An error has occurred in fetching location for listing: " + listingId);
        }

    }

    @Override
    public Location findLocationById(int locationId) {
        final String FIND_LOCATION_BY_ID = """
                SELECT * FROM locations WHERE id = :id
                """;
        log.info("Fetching location for id: {}", locationId);
        try {
            return jdbc.queryForObject(FIND_LOCATION_BY_ID, Map.of("id", locationId), new LocationRowMapper());

        } catch (Exception e) {
            log.error("Error fetching location for id {}", locationId, e.getCause());
            throw new DatabaseException("An error has occurred in fetching location for id: " + locationId);
        }
    }
}

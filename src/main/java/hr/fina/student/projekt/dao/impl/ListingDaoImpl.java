package hr.fina.student.projekt.dao.impl;

import hr.fina.student.projekt.dao.ListingDao;
import hr.fina.student.projekt.entity.Listing;
import hr.fina.student.projekt.exceptions.database.DatabaseException;
import hr.fina.student.projekt.mapper.ListingRowMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Repository
public class ListingDaoImpl implements ListingDao {
    private final NamedParameterJdbcTemplate jdbc;


    public List<Listing> findAllListings() {
        try {
            log.info("Fetching all listings");
            final String FIND_ALL_LISTINGS = "SELECT * FROM listings";
            List<Listing> listings = jdbc.query(FIND_ALL_LISTINGS, new ListingRowMapper());
            return List.of();
        } catch (Exception e) {
            log.error("Error fetching all listings", e.getCause());
            throw new DatabaseException("An error has occured in fetching all listings");
        }

    }
}

package hr.fina.student.projekt.dao;

import hr.fina.student.projekt.entity.Location;

public interface LocationDao {
    Location findLocationByListingId(int listingId);
    Location findLocationById(int locationId);
}

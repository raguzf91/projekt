package hr.fina.student.projekt.dao;

import hr.fina.student.projekt.entity.Listing;
import hr.fina.student.projekt.entity.Location;
import hr.fina.student.projekt.entity.User;
import java.util.List;

public interface ListingDao {
    List<Listing> findAllListings();

    Location findLocationByListingId(int listingId);
}

package hr.fina.student.projekt.dao;

import hr.fina.student.projekt.entity.Listing;
import hr.fina.student.projekt.entity.Location;
import hr.fina.student.projekt.entity.User;
import hr.fina.student.projekt.request.ListingRequest;

import java.util.List;

public interface ListingDao {
    List<Listing> findAllListings();
    Listing findListing(Integer id);
    List<Listing> findListingByCategory(String category);
    List<Listing> findListingsByUserId(Integer userId);
    Double getAverageRatingScore(Integer id);
    void createListing(Listing listing);
}

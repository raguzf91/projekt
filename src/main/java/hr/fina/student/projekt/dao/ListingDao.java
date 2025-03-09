package hr.fina.student.projekt.dao;

import hr.fina.student.projekt.dto.ListingFilter;
import hr.fina.student.projekt.entity.Listing;


import java.util.Date;
import java.util.List;

public interface ListingDao {
    List<Listing> findAllListings();
    Listing findListing(Integer id);
    List<Listing> findListingByCategory(String category);
    List<Listing> findListingsByUserId(Integer userId);
    Double getAverageRatingScore(Integer id);
    void createListing(Listing listing);
    void bookListing(Integer listingId, Integer userId, Date checkIn, Date checkOut, Double paymentAmount, Integer numberOfGuests);
    void deleteListing(Integer listingId);
    List<Listing> findListingsByFilter(ListingFilter listingRequest);
    List<Listing> findLikedListingsByUserId(Integer id);
}

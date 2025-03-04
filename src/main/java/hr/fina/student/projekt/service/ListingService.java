package hr.fina.student.projekt.service;

import hr.fina.student.projekt.entity.Listing;
import hr.fina.student.projekt.request.ListingRequest;

import java.sql.Date;
import java.util.List;

public interface ListingService {
    public List<Listing> getAllListings();
    public Listing getListing(Integer id);
    List<Listing> getListingsByCategory(String category);
    void createListing(ListingRequest listingRequest);
    void bookListing(Integer listingId, String reservationDetails);
    void deleteListing(Integer listingId);
}

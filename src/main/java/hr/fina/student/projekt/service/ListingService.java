package hr.fina.student.projekt.service;

import hr.fina.student.projekt.entity.Listing;
import java.util.List;

public interface ListingService {
    public List<Listing> getAllListings();
    public Listing getListing(Integer id);

    List<Listing> getListingsByCategory(String category);
}

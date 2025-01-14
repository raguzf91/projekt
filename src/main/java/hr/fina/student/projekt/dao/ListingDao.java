package hr.fina.student.projekt.dao;

import hr.fina.student.projekt.entity.Listing;

import java.util.List;

public interface ListingDao {
    List<Listing> findAllListings();
}

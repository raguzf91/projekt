package hr.fina.student.projekt.dao.impl;

import hr.fina.student.projekt.dao.ListingDao;
import hr.fina.student.projekt.dao.LocationDao;
import hr.fina.student.projekt.dao.UserDao;
import hr.fina.student.projekt.entity.*;
import hr.fina.student.projekt.exceptions.database.DatabaseException;
import hr.fina.student.projekt.mapper.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Repository
public class ListingDaoImpl implements ListingDao {
    private final NamedParameterJdbcTemplate jdbc;
    private final LocationDao locationDao;
    private final UserDao userDao;

    public List<Listing> findAllListings() {
        try {
            log.info("Fetching all listings");
            final String FIND_ALL_LISTINGS = "SELECT id, type_of_listing, rating, user_id, price FROM listings";
            List<Listing> listings = jdbc.query(FIND_ALL_LISTINGS, new ListingSecondRowMapper());

            for(Listing listing : listings) {
                if(listing.getLocation() == null) {
                    listing.setLocation(locationDao.findLocationByListingId(listing.getId()));
                }
                if(listing.getUser() == null) {
                    listing.setUser(findUserByListingId(listing));
                }
                if(listing.getPhotos() == null) {
                    listing.setPhotos(findPhotosByListingId(listing));
                }
            }

            return listings;
        } catch (Exception e) {
            log.error("Error fetching all listings", e.getCause());
            throw new DatabaseException("An error has occured in fetching all listings");
        }

    }


    private User findUserByListingId(Listing listing) {
        try{
            final String FIND_USER_BY_LISTING_ID = """
                        SELECT user_id FROM listings WHERE id = :listingId
                    """;
            Integer listingId = listing.getId();
            Integer userId = jdbc.queryForObject(FIND_USER_BY_LISTING_ID, Map.of("listingId", listingId), Integer.class);
            return userDao.findById(userId);
        } catch (Exception e) {
            log.error("Error fetching user for listing id {}", listing.getId(), e.getCause());
            throw new DatabaseException("An error has occurred in fetching user for listing id: " + listing.getId());
        }
    }

    private List<Photo> findPhotosByListingId(Listing listing) {
        try{
            final String FIND_PHOTO_BY_LISTING_ID = """
                        SELECT * FROM photos WHERE listing_id = :listingId
                    """;
            Integer listingId = listing.getId();
            return jdbc.query(FIND_PHOTO_BY_LISTING_ID, Map.of("listingId", listingId), new PhotoRowMapper());
        } catch (Exception e) {
            log.error("Error fetching user for listing id {}", listing.getId(), e.getCause());
            throw new DatabaseException("An error has occurred in fetching user for listing id: " + listing.getId());
        }
    }


    private Location findLocationByListingId(Listing listing) {
        return locationDao.findLocationByListingId(listing.getId());
    }

    @Override
    public Listing findListing(Integer id) {
        try{
            log.info("Fetching listing by id {}", id);
            final String FIND_LISTING_BY_ID = """
                SELECT * FROM listings WHERE id = :id
            """;

            final String FIND_ALL_REVIEWS_BY_LISTING_ID = """
                SELECT * FROM reviews WHERE listing_id = :listingId
            """;

            Listing listing = jdbc.queryForObject(FIND_LISTING_BY_ID, Map.of("id", id), new ListingRowMapper());
            HashSet<Review> reviews = jdbc.query(FIND_ALL_REVIEWS_BY_LISTING_ID, Map.of("listingId", id), new ReviewRowMapper())
                    .stream()
                    .collect(HashSet::new, HashSet::add, HashSet::addAll);  // Collect all reviews into a set

            listing.setReviews(reviews);
            listing.setNumberOfReviews(reviews.size());

            listing.setLocation(findLocationByListingId(listing));
            listing.setUser(findUserByListingId(listing));
            listing.setPhotos(findPhotosByListingId(listing));
            listing.setAmenities(findAmenitiesByListingId(id));

            return listing;
        } catch (Exception e) {
            log.error("Error fetching listing by id {}", id);
            log.error(e.getCause().toString());
            throw new DatabaseException("An error has occurred in fetching listing by id: " + id);
        }

    }

    @Override
    public List<Listing> findListingByCategory(String category) {
        try{
            log.info("Fetching listings by category {}", category);
            final String FIND_LISTING_BY_CATEGORY = """
                SELECT id, description, title, rating, user_id, price FROM listings WHERE category = :category;
            """;
            List<Listing> listings = jdbc.query(FIND_LISTING_BY_CATEGORY, Map.of("category", category), new ListingSecondRowMapper());
            for(Listing listing : listings) {
                listing.setLocation(findLocationByListingId(listing));
                listing.setUser(findUserByListingId(listing));
                listing.setPhotos(findPhotosByListingId(listing));
            }
            return listings;
        } catch (Exception e) {
            log.error("Error fetching listings by category " + e.getCause() );
            throw new DatabaseException("An error has occurred in fetching listings by category: " + category);
        }
    }

    private List<Amenity> findAmenitiesByListingId(Integer listingId) {
        try{
            final String FIND_AMENITIES_BY_LISTING_ID = """
            SELECT id, description, icon FROM amenities WHERE id IN (
                SELECT amenities_id FROM listingamenities WHERE listing_id = :listingId AND enabled = true
            )
        """;
            return jdbc.query(FIND_AMENITIES_BY_LISTING_ID, Map.of("listingId", listingId), new AmenitiesRowMapper());
        } catch (Exception e) {
            log.error("Error fetching amenities for listing id {}", listingId);
            log.error(e.getCause().toString());
            throw new DatabaseException("An error has occurred in fetching amenities for listing id: " + listingId);
        }
    }

}

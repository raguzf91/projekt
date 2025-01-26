package hr.fina.student.projekt.dao.impl;

import hr.fina.student.projekt.dao.ListingDao;
import hr.fina.student.projekt.dao.LocationDao;
import hr.fina.student.projekt.dao.UserDao;
import hr.fina.student.projekt.dto.UserDTO;
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
import java.util.stream.Collectors;

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

    private Double calculateAverageRatingScore(List<Double> ratings) {
        return ratings.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
    }

    public Double getAverageRatingScore(Integer id) {
        final String INSERT_AVERAGE_RATING = """
            UPDATE users SET average_rating = :averageRating WHERE id = :id
        """;
        try {
            List<Listing> listings = findListingsByUserId(id);
            List<Double> ratings = getRatingsForAllListing(listings);
            
            Double averageRating = calculateAverageRatingScore(ratings);
            jdbc.update(INSERT_AVERAGE_RATING, Map.of("averageRating", averageRating, "id", id));
            return averageRating;
        } catch (Exception e) {
            log.error("Error fetching average rating score for user id {}", id);
            log.error(e.getCause().toString());
            throw new DatabaseException("An error has occurred in fetching average rating score for user id: " + id);
        }
       
        
        
    }

    private List<Double> getRatingsForAllListing (List<Listing> listings) {
        return listings.stream().map(Listing::getRating).collect(Collectors.toList());

    }

    public List<Listing> findListingsByUserId(Integer userId) {
        try{
            log.info("Fetching listings by user id {}", userId);
            final String FIND_LISTINGS_BY_USER_ID = """
                SELECT * FROM listings WHERE user_id = :userId
            """;


            List<Listing> listings = jdbc.query(FIND_LISTINGS_BY_USER_ID, Map.of("userId", userId), new ListingSecondRowMapper());
            for(Listing listing : listings) {
                listing.setLocation(findLocationByListingId(listing));
                listing.setPhotos(findPhotosByListingId(listing));
            }

            return listings;
        } catch (Exception e) {
            log.error("Error fetching listings by user id {}", userId);
            log.error(e.getCause().toString());
            throw new DatabaseException("An error has occurred in fetching listings by user id: " + userId);
        }
    }

    


    private UserDTO findUserByListingId(Listing listing) {
        try{
            final String FIND_USER_BY_LISTING_ID = """
                        SELECT user_id FROM listings WHERE id = :listingId
                    """;
            Integer listingId = listing.getId();
            Integer userId = jdbc.queryForObject(FIND_USER_BY_LISTING_ID, Map.of("listingId", listingId), Integer.class);
            User user = userDao.findById(userId);
            Double averageRating = getAverageRatingScore(userId);
            user.setAverageRating(averageRating);
            return UserDTOMapper.fromUser(user);
        } catch (Exception e) {
            log.error("Error fetching user for listing id {}", listing.getId());
            log.error(e.getCause().toString());
            throw new DatabaseException("An error has occurred in fetching user for listing id: " + listing.getId());
        }
    }

    private List<Photo> findPhotosByListingId(Listing listing) {
        try{
            final String FIND_PHOTO_BY_LISTING_ID = """
            SELECT id, photo_url, name, bedroom_photo FROM photos WHERE id IN (
                SELECT photo_id FROM listingphotos WHERE listing_id = :listingId
            )
                    """;
            Integer listingId = listing.getId();
            return jdbc.query(FIND_PHOTO_BY_LISTING_ID, Map.of("listingId", listingId), new PhotoRowMapper());
        } catch (Exception e) {
            log.error("Error fetching photos for listing id {}", listing.getId());
            log.error(e.getCause().toString());
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

            final String INSERT_LISTING_RATING = """
                UPDATE listings SET rating = :rating WHERE id = :id
            """;

            Listing listing = jdbc.queryForObject(FIND_LISTING_BY_ID, Map.of("id", id), new ListingRowMapper());
            List<Review> reviews = jdbc.query(FIND_ALL_REVIEWS_BY_LISTING_ID, Map.of("listingId", id), new ReviewRowMapper());
                   
            reviews.stream().forEach(review -> review.setAuthor(userDao.findById(review.getUserId())));
            listing.setReviews(reviews);
            listing.setNumberOfReviews(reviews.size());
            listing.setRating(calculateTotalReviewScore(reviews));
            jdbc.update(INSERT_LISTING_RATING, Map.of("rating", listing.getRating(), "id", id));
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

            final String INSERT_LISTING_RATING = """
                UPDATE listings SET rating = :rating WHERE id = :id
            """;

            final String FIND_ALL_REVIEWS_BY_LISTING_ID = """
                SELECT * FROM reviews WHERE listing_id = :listingId
            """;

            List<Listing> listings = jdbc.query(FIND_LISTING_BY_CATEGORY, Map.of("category", category), new ListingSecondRowMapper());
            for(Listing listing : listings) {
                listing.setLocation(findLocationByListingId(listing));
                listing.setUser(findUserByListingId(listing));
                listing.setPhotos(findPhotosByListingId(listing));
                List<Review> reviews = jdbc.query(FIND_ALL_REVIEWS_BY_LISTING_ID, Map.of("listingId", listing.getId()), new ReviewRowMapper());
                reviews.stream().forEach(review -> review.setAuthor(userDao.findById(review.getUserId())));
                listing.setReviews(reviews);
                listing.setNumberOfReviews(reviews.size());
                listing.setRating(calculateTotalReviewScore(reviews));
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

    private double calculateTotalReviewScore(List<Review> reviews) {
        double numOfReviews = reviews.size();
        double sumOfScores = reviews.stream().mapToDouble(Review::getNumberOfStars).sum();
        return sumOfScores / numOfReviews;
    }

}

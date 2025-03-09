package hr.fina.student.projekt.dao.impl;

import hr.fina.student.projekt.dao.AmenityDao;
import hr.fina.student.projekt.dao.ListingDao;
import hr.fina.student.projekt.dao.LocationDao;
import hr.fina.student.projekt.dao.UserDao;
import hr.fina.student.projekt.dto.ListingFilter;
import hr.fina.student.projekt.dto.UserDTO;
import hr.fina.student.projekt.entity.*;
import hr.fina.student.projekt.exceptions.database.DatabaseException;
import hr.fina.student.projekt.mapper.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
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
    private final AmenityDao amenityDao;

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
            Double totalReviewScore = calculateTotalReviewScore(reviews);
            if(totalReviewScore.isNaN()) {
                listing.setRating(1.00);
            } else {
                listing.setRating(totalReviewScore);
            }
            
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
            SELECT l.id, l.description, l.title, l.rating, l.user_id, l.price, l.type_of_listing 
            FROM listings l
            JOIN listingamenities la ON l.id = la.listing_id
            JOIN amenities a ON la.amenities_id = a.id
            WHERE a.description = :category
        """;

           
            if(category.equals("Sve")) {
                return findAllListings();
            }

            List<Listing> listings = jdbc.query(FIND_LISTING_BY_CATEGORY, Map.of("category", category), new ListingSecondRowMapper());

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

    private SqlParameterSource getSqlParameterSource(Listing listing) {
        return new MapSqlParameterSource()
                .addValue("userId", listing.getUser().getId())
                .addValue("title", listing.getTitle())
                .addValue("description", listing.getDescription())
                .addValue("price", listing.getPrice())
                .addValue("cleaningFee", listing.getCleaningFee())
                .addValue("refundable", listing.isRefundable())
                .addValue("maxGuests", listing.getMaxGuests())
                .addValue("numberOfBedrooms", listing.getNumberOfBedrooms())
                .addValue("numberOfBeds", listing.getNumberOfBeds())
                .addValue("numberOfBathrooms", listing.getNumberOfBathrooms())
                .addValue("typeOfListing", listing.getTypeOfListing());
            
    }

    private SqlParameterSource getSqlParameterSource(Location location) {
        return new MapSqlParameterSource()
            .addValue("streetNumber", location.getStreetNumber())
            .addValue("street", location.getStreet())
            .addValue("city", location.getCity())
            .addValue("country", location.getCountry())
            .addValue("postalCode", location.getPostalCode())
            .addValue("latitude", location.getLatitude())
            .addValue("longitude", location.getLongitude())
            .addValue("fullAddress", location.getFullAddress());
            
    }

    private SqlParameterSource getSqlParameterSource(Photo photo, Integer userId) {
        return new MapSqlParameterSource()
            .addValue("photoUrl", photo.getPhotoUrl())
            .addValue("name", photo.getName())
            .addValue("bedroomPhoto", photo.getBedroomPhoto())
            .addValue("userId", userId);
    }


    private void insertLocation(Listing listing, Integer listingId) {
        final String INSERT_LOCATION = """
            INSERT INTO locations (country, city, street, postal_code, longitude, latitude, listing_id, full_address) 
            VALUES (:country, :city, :street, :postalCode, :longitude, :latitude, :listingId, :fullAddress)
        """;
        jdbc.update(INSERT_LOCATION, ((MapSqlParameterSource) getSqlParameterSource(listing.getLocation())).addValue("listingId", listingId));
    }

    private void insertPhotos(List<Photo> photos, Integer listingId) {
        final String INSERT_PHOTO = """
            INSERT INTO photos (photo_url, name, bedroom_photo, user_id) 
            VALUES (:photoUrl, :name, :bedroomPhoto, :userId)
        """;
        final String INSERT_LISTING_PHOTO = """
            INSERT INTO listingphotos (listing_id, photo_id) 
            VALUES (:listingId, :photoId)
        """;
        Listing listing = findListing(listingId);
        Integer userId = listing.getUser().getId();
        for(Photo photo : photos) {
            KeyHolder holder = new GeneratedKeyHolder();
            jdbc.update(INSERT_PHOTO, getSqlParameterSource(photo, userId), holder, new String[] {"id"});
            Integer photoId = (Integer)holder.getKey();
            jdbc.update(INSERT_LISTING_PHOTO, Map.of("listingId", listingId, "photoId", photoId));
        }
    }

    private void insertAmenities(List<Amenity> amenities, Integer listingId) {
        
        final String INSERT_LISTING_AMENITY = """
            INSERT INTO listingamenities (listing_id, amenities_id, enabled) 
            VALUES (:listingId, :amenitiesId, true)
        """;
        for(Amenity amenity : amenities) {   
            
            jdbc.update(INSERT_LISTING_AMENITY, Map.of("listingId", listingId, "amenitiesId", amenity.getId()));
        }
    }


    @Override
    public void createListing(Listing listing) {
        try {
            log.info("Creating listing");
            final String INSERT_LISTING = """
                INSERT INTO listings (title, description, price, cleaning_fee, refundable, maximum_guests, number_of_bedrooms, number_of_beds, number_of_bathrooms, type_of_listing, user_id) 
                VALUES (:title, :description, :price, :cleaningFee, :refundable, :maxGuests, :numberOfBedrooms, :numberOfBeds, :numberOfBathrooms, :typeOfListing, :userId)
            """;
            SqlParameterSource params = getSqlParameterSource(listing);
            KeyHolder holder = new GeneratedKeyHolder();
            jdbc.update(INSERT_LISTING, params, holder, new String[] {"id"});

            listing.setId((Integer)holder.getKey());
            Integer listingId = listing.getId();

            insertLocation(listing, listingId);
            insertPhotos(listing.getPhotos(), listingId);
            insertAmenities(listing.getAmenities(), listingId);
        } catch (Exception e) {
            log.error("Error creating listing", e.getCause());
            throw new DatabaseException("An error has occurred in creating listing");
        }
    }

    @Override
    public void bookListing(Integer listingId, Integer userId, Date checkIn, Date checkOut, Double paymentAmount,
            Integer numberOfGuests) {
       try {
            log.info("Booking listing");
            final String INSERT_RESERVATION = """
                INSERT INTO reservations (reserved_from, reserved_until, user_id, listing_id, payment_amount, number_of_guests, canceled) 
                VALUES (:checkIn, :checkOut, :userId, :listingId, :paymentAmount, :numberOfGuests, :canceled)
            """;
            jdbc.update(INSERT_RESERVATION, Map.of("checkIn", checkIn, "checkOut", checkOut, "userId", userId, "listingId", listingId, "paymentAmount", paymentAmount, "numberOfGuests", numberOfGuests, "canceled", false));
        } catch (Exception e) {
            log.error("Error booking listing", e.getCause());
            throw new DatabaseException("An error has occurred in booking listing");
       }
    }

    @Override
    public void deleteListing(Integer listingId) {
        try {
            log.info("Deleting listing");
            final String DELETE_LISTING = """
                DELETE FROM listings WHERE id = :listingId
            """;
            jdbc.update(DELETE_LISTING, Map.of("listingId", listingId));
        } catch (Exception e) {
            log.error("Error deleting listing", e.getCause());
            throw new DatabaseException("An error has occurred in deleting listing");
        }
    }

    private Date convertStringToDate(String dateString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate localDate = LocalDate.parse(dateString, formatter);
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    };

    @Override
    public List<Listing> findListingsByFilter(ListingFilter listingFilter) {
        StringBuilder FIND_LISTINGS_BY_FILTER = new StringBuilder("SELECT id, type_of_listing, rating, user_id, price FROM listings WHERE 1=1 ");
        MapSqlParameterSource params = new MapSqlParameterSource();

        if(listingFilter.getMinimalPrice() != null) {
            FIND_LISTINGS_BY_FILTER.append(" AND price >= :minimalPrice ");
            params.addValue("minimalPrice", listingFilter.getMinimalPrice());
        }
        if(listingFilter.getMaximalPrice() != null) {
            FIND_LISTINGS_BY_FILTER.append(" AND price <= :maximalPrice ");
            params.addValue("maximalPrice", listingFilter.getMaximalPrice());
        }
        if(listingFilter.getBedrooms() != null) {
            FIND_LISTINGS_BY_FILTER.append(" AND number_of_bedrooms = :bedrooms ");
            params.addValue("bedrooms", listingFilter.getBedrooms());
        }
        if(listingFilter.getBeds() != null) {
            FIND_LISTINGS_BY_FILTER.append(" AND number_of_beds = :beds ");
            params.addValue("beds", listingFilter.getBeds());
        }
        if(listingFilter.getBathrooms() != null) {
            FIND_LISTINGS_BY_FILTER.append(" AND number_of_bathrooms = :bathrooms ");
            params.addValue("bathrooms", listingFilter.getBathrooms());
        }
        if(listingFilter.getLocation() != null) {
            FIND_LISTINGS_BY_FILTER.append(
               " AND id IN ( " +
                        "SELECT la.listing_id FROM listingamenities la " +
                        "JOIN amenities a ON la.amenities_id = a.id " +
                        "WHERE a.description = :location " +
                    ")"
            );
            params.addValue("location", listingFilter.getLocation());
        }

        if(listingFilter.getTypeOfListing() != null) {
            FIND_LISTINGS_BY_FILTER.append(" AND type_of_listing = :typeOfListing ");
            params.addValue("typeOfListing", listingFilter.getTypeOfListing());
        }

        if(listingFilter.getAmenities() != null && !listingFilter.getAmenities().isEmpty()) {
            FIND_LISTINGS_BY_FILTER.append(
                " AND id IN ( " +
                    "SELECT listing_id FROM listingamenities la " +
                    "JOIN amenities a ON la.amenities_id = a.id " +
                    "WHERE a.description IN (:amenities) " +
                ")"
            );
            params.addValue("amenities", listingFilter.getAmenities());
        }

        if(listingFilter.getSpeaksLanguages() != null && !listingFilter.getSpeaksLanguages().isEmpty() ) {
            FIND_LISTINGS_BY_FILTER.append(
                " AND id IN ( " +
                    "SELECT l.id FROM listings l " +
                    "JOIN users u ON l.user_id = u.id " +
                    "WHERE u.speaks_languages && CAST(:speaksLanguages AS text[])" +
                ")"
            );
            params.addValue("speaksLanguages", listingFilter.getSpeaksLanguages().toArray(new String[0]));
        }

        if(listingFilter.getFullAddress() != null && !listingFilter.getFullAddress().isEmpty()) {
            FIND_LISTINGS_BY_FILTER.append(" AND id IN (SELECT listing_id FROM locations WHERE full_address ILIKE :fullAddress) ");
            params.addValue("fullAddress", "%" + listingFilter.getFullAddress() + "%");

            
        }

        

        if(listingFilter.getArrival() != null && listingFilter.getDeparture() != null) {
            Date arrival = null;
            Date departure = null;
            if(listingFilter.getArrival().getClass() == String.class && listingFilter.getDeparture().getClass() == String.class) {
                arrival = convertStringToDate((String)listingFilter.getArrival());
                departure = convertStringToDate((String)listingFilter.getDeparture());
            }
            
            FIND_LISTINGS_BY_FILTER.append(
                " AND NOT EXISTS ( " +
                    "SELECT 1 FROM reservations r " +
                    "WHERE r.listing_id = listings.id " +
                    "AND (:arrival < r.reserved_until AND :departure > r.reserved_from) " +
                ") "
            );
            params.addValue("arrival", arrival);
            params.addValue("departure", departure);
        }

        if(listingFilter.getNumberOfGuests() != null) {
            FIND_LISTINGS_BY_FILTER.append(
               " AND maximum_guests >= :numberOfGuests"
            );
            params.addValue("numberOfGuests", listingFilter.getNumberOfGuests());
        }

        try {
            List<Listing> listings = jdbc.query(FIND_LISTINGS_BY_FILTER.toString(), params, new ListingSecondRowMapper());
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
        } catch (EmptyResultDataAccessException emptyResultDataAccessException) {
            log.error("No listings found with given filters");
            return null;
        } catch (Exception e) {
            log.error("Error fetching listings by filter", e.getCause());
            throw new DatabaseException("An error has occurred in fetching listings by filter");
        }


    }

    
    @Override
    public List<Listing> findLikedListingsByUserId(Integer id) {        final String FIND_LIKED_LISTINGS = """
                SELECT id, type_of_listing, rating, user_id, price FROM listings WHERE id IN (SELECT listing_id FROM likedlistings WHERE user_id = :id)
                """;
        try {
            List<Listing> listings = jdbc.query(FIND_LIKED_LISTINGS, Map.of("id", id), new ListingSecondRowMapper());
            if(listings.isEmpty()) {
                return null;
            }
            
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
            log.error("Error finding liked listings: " + e.getCause());
            throw new DatabaseException("An error occured in finding the liked listings");
        }
  
    
    
}
}

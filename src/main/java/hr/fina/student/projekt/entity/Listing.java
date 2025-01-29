package hr.fina.student.projekt.entity;

import java.util.Set;
import java.util.List;

import hr.fina.student.projekt.dto.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Listing extends BaseEntity {
    
    private String title;
    private String description;
    private Double price;
    private Double rating;
    private boolean refundable;
    private Integer numberOfBedrooms;
    private Integer numberOfBeds;
    private UserDTO user;
    private List<Photo> photos;
    private Set<Reservation> reservations;
    private List<Review> reviews;
    private Location location;
    private List<Rule> rules;
    private List<Amenity> amenities;
    private Integer maxGuests;
    private Integer numberOfReviews;
    private String typeOfListing;

    private Integer numberOfBathrooms;
    private Double cleaningFee;


   

    
}

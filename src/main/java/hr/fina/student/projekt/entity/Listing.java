package hr.fina.student.projekt.entity;

import java.util.Set;
import java.util.List;
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
    private String desctiotion;
    private Double price;
    private Double rating;
    private boolean refundable;
    private Integer numberOfBedrooms;
    private Integer numberOfBeds;
    private User user;
    private Set<Photo> photos;
    private Set<Reservation> reservations;
    private Set<Review> reviews;
    private Location location;
    private List<Rule> rules;
    private Set<Amenity> amenities;

   

    
}

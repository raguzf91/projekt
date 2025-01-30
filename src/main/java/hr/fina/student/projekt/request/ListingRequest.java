package hr.fina.student.projekt.request;

import java.io.Serializable;
import java.util.List;

import hr.fina.student.projekt.entity.Location;
import lombok.AllArgsConstructor;
import lombok.Data;
import hr.fina.student.projekt.entity.Photo;
import jakarta.validation.constraints.NotBlank;
@Data
@AllArgsConstructor
public class ListingRequest implements Serializable{
    
    @NotBlank
    private Integer userId;
    @NotBlank
    private String title;
    @NotBlank
    private String description;
    @NotBlank
    private double price;
    @NotBlank
    private double cleaningFee;
    @NotBlank
    private boolean refundable;
    @NotBlank
    private int maxGuests;
    @NotBlank
    private int numberOfBedrooms;
    @NotBlank
    private int numberOfBeds;
    @NotBlank
    private int numberOfBathrooms;
    @NotBlank
    private String typeOfListing;
    @NotBlank
    private List<Photo> photos;
    @NotBlank
    private List<String> amenities;
    @NotBlank
    private Location fullLocation;

    


    
}
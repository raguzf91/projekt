package hr.fina.student.projekt.request;

import java.util.List;
import lombok.Data;

@Data
public class ListingRequest {
    private String title;
    private String description;
    private double price;
    private double cleaningFee;
    private boolean refundable;
    private int maxGuests;
    private int numberOfBedrooms;
    private int numberOfBeds;
    private int numberOfBathrooms;
    private String fullAddress;
    private double latitude;
    private double longitude;
    private String typeOfListing;
    private List<Photo> photos;
    private List<String> amenities;
    private FullLocation fullLocation;

    @Data
    public static class Photo {
        private String photoUrl;
        private String name;
    }

    @Data
    public static class FullLocation {
        private String streetNumber;
        private String street;
        private String city;
        private String country;
        private String postalCode;
        private double latitude;
        private double longitude;
    }
}
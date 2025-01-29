package hr.fina.student.projekt.service.impl;

import hr.fina.student.projekt.dao.ListingDao;
import hr.fina.student.projekt.entity.Listing;
import hr.fina.student.projekt.entity.Photo;
import hr.fina.student.projekt.request.ListingRequest;
import hr.fina.student.projekt.service.AmenityService;
import hr.fina.student.projekt.service.ListingService;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ListingServiceImpl implements ListingService {
    private final ListingDao listingDao;
    private final AmenityService amenityService;

    @Override
    public List<Listing> getAllListings() {
        return listingDao.findAllListings();
    }

    @Override
    public Listing getListing(Integer id) {
        return listingDao.findListing(id);
    }

    @Override
    public List<Listing> getListingsByCategory(String category) {
        return listingDao.findListingByCategory(category);
    }

    @Override
    public void createListing(ListingRequest listingRequest) {
        //convert request to entity
        try {
            Listing listing = Listing.builder()
            .title(listingRequest.getTitle())
            .description(listingRequest.getDescription())
            .price(listingRequest.getPrice())
            .cleaningFee(listingRequest.getCleaningFee())
            .refundable(listingRequest.isRefundable())
            .maxGuests(listingRequest.getMaxGuests())
            .numberOfBedrooms(listingRequest.getNumberOfBedrooms())
            .numberOfBeds(listingRequest.getNumberOfBeds())
            .numberOfBathrooms(listingRequest.getNumberOfBathrooms())
            .photos(listingRequest.getPhotos().stream().map(photo -> Photo.builder()
                    .photoUrl(photo.getPhotoUrl())
                    .name(photo.getName())
                    .bedroomPhoto(photo.getBedroomPhoto())
                    .build()).collect(Collectors.toList()))
            .amenities(listingRequest.getAmenities().stream().map(amenity -> amenityService.findAmenitiesByDescription(amenity)).collect(Collectors.toList()))
            .location(listingRequest.getFullLocation())
            .typeOfListing(listingRequest.getTypeOfListing())
            .build();
    
        listingDao.createListing(listing);

        } catch (Exception e) {
            log.error("Error creating listing", e);
            throw new RuntimeException("Error creating listing");
        }
      

    }


}

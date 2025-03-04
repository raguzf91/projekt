package hr.fina.student.projekt.service.impl;

import hr.fina.student.projekt.dao.ListingDao;
import hr.fina.student.projekt.entity.Listing;
import hr.fina.student.projekt.entity.Photo;
import hr.fina.student.projekt.mapper.UserDTOMapper;
import hr.fina.student.projekt.request.ListingRequest;
import hr.fina.student.projekt.service.AmenityService;
import hr.fina.student.projekt.service.ListingService;
import hr.fina.student.projekt.service.UserService;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ListingServiceImpl implements ListingService {
    private final ListingDao listingDao;
    private final AmenityService amenityService;
    private final UserService userService;
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
            .user(UserDTOMapper.fromUser(userService.findUserById((listingRequest.getUserId()))))
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

    @Override
    public void bookListing(Integer listingId, String reservationDetails) {
        try {
            Map<String, Object> detailsMap = new ObjectMapper().readValue(reservationDetails, new TypeReference<Map<String, Object>>() {});
            log.debug(detailsMap.toString());

            Object checkInObj = detailsMap.get("checkIn");
            Date checkIn;
            if (checkInObj instanceof String) {
                String s = (String) checkInObj;
                // If the string contains non-digits, parse using a formatter
                if (!s.matches("\\d+")) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                    LocalDate localDate = LocalDate.parse(s, formatter);
                    checkIn = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
                } else {
                    checkIn = new Date(Long.parseLong(s));
                }
            } else {
                checkIn = new Date((Long) checkInObj);
            }
            Object checkOutObj = detailsMap.get("checkOut");
            Date checkOut;
            if (checkOutObj instanceof String) {
                String s = (String) checkOutObj;
                // If the string contains non-digits, parse using a formatter
                if (!s.matches("\\d+")) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                    LocalDate localDate = LocalDate.parse(s, formatter);
                    checkOut = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
                } else {
                    checkOut = new Date(Long.parseLong(s));
                }
            } else {
                checkOut = new Date((Long) checkInObj);
            }
            Integer userId = (Integer) detailsMap.get("userId");
            Object paymentAmountObj = detailsMap.get("paymentAmount");
            Double paymentAmount = paymentAmountObj instanceof Integer ? ((Integer) paymentAmountObj).doubleValue() : (Double) paymentAmountObj;
            Integer numberOfGuests = Integer.valueOf( (String) detailsMap.get("numberOfGuests"));
            listingDao.bookListing(listingId, userId, checkIn, checkOut, paymentAmount, numberOfGuests);

        } catch (JsonMappingException jsonMappingException) {
            throw new RuntimeException("Error mapping user details");
        } catch (JsonProcessingException jsonProcessingException) {
            throw new RuntimeException("Error processing user details");
        } catch (Exception e) {
            throw new RuntimeException("Error booking listing");
        }
    }

    @Override
    public void deleteListing(Integer listingId) {
        try {
            listingDao.deleteListing(listingId);
        } catch (Exception e) {
            throw new RuntimeException("Error deleting listing");
        }
    }
        

   


}

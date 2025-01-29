package hr.fina.student.projekt.controller;

import hr.fina.student.projekt.entity.Listing;
import hr.fina.student.projekt.entity.Location;
import hr.fina.student.projekt.entity.Photo;
import hr.fina.student.projekt.request.ListingRequest;
import hr.fina.student.projekt.response.HttpResponse;
import hr.fina.student.projekt.service.ListingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.time.LocalDateTime.now;
import static org.springframework.http.HttpStatus.OK;


@RestController
@RequestMapping("/api/listing")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin("*")
public class ListingController {
    private final ObjectMapper objectMapper;
    public final ListingService listingService;
    @GetMapping("/all")
    public ResponseEntity<HttpResponse> getAllListings() {

        List<Listing> listings = listingService.getAllListings();
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(now().toString())
                        .data(Map.of("listings", listings))
                        .message("All listings fetched successfully")
                        .status(OK)
                        .statusCode(OK.value())
                        .build()
        );
    }
    @GetMapping("/{id}")
    public ResponseEntity<HttpResponse> getListing(@PathVariable("id") Integer id) {
        Listing listing = listingService.getListing(id);
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(now().toString())
                        .data(Map.of("listingData", listing))
                        .message("Listing fetched successfully")
                        .status(OK)
                        .statusCode(OK.value())
                        .build()
        );
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<HttpResponse> getListingByCategory(@PathVariable("category") String category) {
        List<Listing> listings = listingService.getListingsByCategory(category);
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(now().toString())
                        .data(Map.of("listings", listings))
                        .message("All listings fetched successfully")
                        .status(OK)
                        .statusCode(OK.value())
                        .build()
        );
    }

    @PostMapping("/create")
    public ResponseEntity<HttpResponse> createListing(
            @RequestBody ListingRequest listingRequest) {

        
       

        listingService.createListing(listingRequest);
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(now().toString())
                        .message("Listing created successfully")
                        .status(OK)
                        .statusCode(OK.value())
                        .build()
        );
    }

}

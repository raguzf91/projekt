package hr.fina.student.projekt.controller;

import hr.fina.student.projekt.entity.Listing;
import hr.fina.student.projekt.response.HttpResponse;
import hr.fina.student.projekt.service.ListingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.time.LocalDateTime.now;
import static org.springframework.http.HttpStatus.OK;


@Controller
@RequestMapping("/api/listing")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin("*")
public class ListingController {

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


}

package hr.fina.student.projekt.controller;

import hr.fina.student.projekt.service.AmenityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import hr.fina.student.projekt.response.HttpResponse;
import hr.fina.student.projekt.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import static java.time.LocalDateTime.now;
import static org.springframework.http.HttpStatus.OK;
import java.util.Map;

@RestController
@RequestMapping("/api/amenity")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin("*")
public class AmenityController {
    private final AmenityService amenityService;

    @GetMapping("/all")
    public ResponseEntity<HttpResponse> getAmenities() {
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(now().toString())
                        .data(Map.of("amenities", amenityService.getAllAmenities()))
                        .message("Amenities fetched successfully")
                        .status(OK)
                        .statusCode(OK.value())
                        .build()
        );
    }
}

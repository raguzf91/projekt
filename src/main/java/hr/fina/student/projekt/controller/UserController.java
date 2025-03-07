package hr.fina.student.projekt.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import hr.fina.student.projekt.entity.Reservation;
import hr.fina.student.projekt.response.HttpResponse;
import hr.fina.student.projekt.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import static java.time.LocalDateTime.now;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;

import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.RequestParam;
import static java.lang.Integer.parseInt;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;




@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin("*")
public class UserController {
    private final UserService userService;
    
    @GetMapping("/reviews/{id}")
    public ResponseEntity<HttpResponse> getReviews(@PathVariable("id") Integer id) {
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(now().toString())
                        .data(Map.of("reviews", userService.getReviews(id)))
                        .message("Reviews fetched successfully")
                        .status(OK)
                        .statusCode(OK.value())
                        .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<HttpResponse> getUser(@PathVariable("id") Integer id) {
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .data(Map.of("user", userService.findUserById((id))))
                        .message("User fetched successfully")
                        .status(OK)
                        .statusCode(OK.value())
                        .build()
        );
    }

    @PutMapping("/{id}/edit-profile")
    public ResponseEntity<HttpResponse> editProfile(@PathVariable String id, @RequestBody String userDetails) {
        if(userService.updateUser(Integer.parseInt(id), userDetails)) {
            return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(now().toString())
                        .message("User profile updated successfully")
                        .status(OK)
                        .statusCode(OK.value())
                        .build()
            );
        } else {
            return ResponseEntity.internalServerError().body(
                HttpResponse.builder()
                        .timeStamp(now().toString())
                        .message("Problem occured in editing profile")
                        .status(INTERNAL_SERVER_ERROR)
                        .statusCode(INTERNAL_SERVER_ERROR.value())
                        .build()
            );
        }

        }

        @GetMapping("/{id}/reservations")
        public ResponseEntity<HttpResponse> getReservations(@PathVariable("id") Integer id) {
            return ResponseEntity.ok().body(
                    HttpResponse.builder()
                            .timeStamp(now().toString())
                            .data(Map.of("reservations", userService.findReservationsByUserId(id)))
                            .message("Reservations fetched successfully")
                            .status(OK)
                            .statusCode(OK.value())
                            .build()
            );
        }

        @DeleteMapping("/{id}/reservations/{reservationId}")
        public ResponseEntity<HttpResponse> deleteReservation(@PathVariable("id") Integer id, @PathVariable("reservationId") Integer reservationId) {
            List<Reservation> reservations = userService.deleteReservation(reservationId, id); 
                return ResponseEntity.ok().body(
                    HttpResponse.builder()
                            .timeStamp(now().toString())
                            .data(Map.of("reservations", reservations))
                            .message("Reservation deleted successfully")
                            .status(OK)
                            .statusCode(OK.value())
                            .build()
                );
         
            }

            @GetMapping("/{id}/reservations/listing/{listingId}")
            public ResponseEntity<HttpResponse> getListingReservations(@PathVariable("id") Integer id, @PathVariable("listingId") Integer listingId) {
                return ResponseEntity.ok().body(
                        HttpResponse.builder()
                                .timeStamp(now().toString())
                                .data(Map.of("reservations", userService.findReservationsByListingId(listingId)))
                                .message("Reservations fetched successfully")
                                .status(OK)
                                .statusCode(OK.value())
                                .build()
                );
            }
            
        
            @GetMapping("/{id}/reservations/listing/{listingId}/is-reserved")
            public ResponseEntity<HttpResponse> getReservation(@PathVariable("id") Integer id, @PathVariable("listingId") Integer listingId) {
                return ResponseEntity.ok().body(
                        HttpResponse.builder()
                                .timeStamp(now().toString())
                                .data(Map.of("isReserved", userService.findReservation(listingId, id)))
                                .message("Reservation fetched successfully")
                                .status(OK)
                                .statusCode(OK.value())
                                .build()
                );
            }

            @PostMapping("/{id}/like-listing/{listingId}")
            public ResponseEntity<HttpResponse> likeListing(@PathVariable("id") Integer id, @PathVariable("listingId") Integer listingId) {
                    return ResponseEntity.ok().body(
                        HttpResponse.builder()
                                .timeStamp(now().toString())
                                .data(Map.of("isLiked", userService.likeListing(id, listingId)))
                                .message("Listing liked successfully")
                                .status(OK)
                                .statusCode(OK.value())
                                .build()
                    );
                
            }

            @GetMapping("/{id}/is-listing-liked/{listingId}")
            public ResponseEntity<HttpResponse> isListingLiked(@PathVariable("id") Integer id, @PathVariable("listingId") Integer listingId) {
                return ResponseEntity.ok().body(
                        HttpResponse.builder()
                                .timeStamp(now().toString())
                                .data(Map.of("isLiked", userService.isListingLiked(id, listingId)))
                                .message("Listing liked fetched successfully")
                                .status(OK)
                                .statusCode(OK.value())
                                .build()
                );
            }
            

           
        
        
        }
        
       
    


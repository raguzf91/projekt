package hr.fina.student.projekt.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.*;

import hr.fina.student.projekt.dto.UserDTO;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Reservation extends BaseEntity {

    private LocalDateTime reservedFrom;
    private LocalDateTime reservedUntil;
    private Listing listing;
    private Integer listingId;
    private Integer numberOfGuests;
    private Double paymentAmount;
    private Integer userId;
    private Boolean canceled;
    
}

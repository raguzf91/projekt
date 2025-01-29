package hr.fina.student.projekt.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class Location extends BaseEntity{
    
    private String street;
    private String streetNumber;
    private String city;
    private String postalCode;
    private String country;
    private Double latitude;
    private Double longitude;
    private String fullAddress;
}

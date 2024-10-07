package hr.fina.student.projekt.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class Location extends BaseEntity{
    
    private String address;
    private String city;
    private String state;
    private String zipcode;
}

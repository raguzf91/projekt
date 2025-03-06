package hr.fina.student.projekt.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ListingFilter {
    private Integer minimalPrice;
    private Integer maximalPrice;
    private Integer bedrooms;
    private Integer beds;
    private Integer bathrooms;
    private String location;
    private String typeOfListing;
    private List<String> amenities;
    private List<String> speaksLanguages;

}

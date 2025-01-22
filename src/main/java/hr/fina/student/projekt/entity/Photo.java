package hr.fina.student.projekt.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Photo extends BaseEntity {
    
    private String photoUrl;
    private Integer listingId;
    private Boolean bedroomPhoto;
}

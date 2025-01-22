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
public class Amenity extends BaseEntity{
    
    private String description;
    private String icon;
    private Boolean enabled;
}

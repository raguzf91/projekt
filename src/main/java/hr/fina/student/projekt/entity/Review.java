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
public class Review extends BaseEntity {

    private String description;
    private Integer numberOfStars;
    private Integer userId;

}

package hr.fina.student.projekt.mapper;

import hr.fina.student.projekt.entity.Review;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ReviewRowMapper implements RowMapper<Review> {
    @Override
    public Review mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Review.builder()
                .id(rs.getInt("id"))
                .description(rs.getString("description"))
                .numberOfStars(rs.getInt("number_of_stars"))
                .userId(rs.getInt("user_id"))
                .build();
    }
}

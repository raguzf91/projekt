package hr.fina.student.projekt.mapper;

import hr.fina.student.projekt.entity.Review;
import org.springframework.jdbc.core.RowMapper;
import java.time.LocalDate;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class ReviewRowMapper implements RowMapper<Review> {
    @Override
    public Review mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Review.builder()
                .id(rs.getInt("id"))
                .description(rs.getString("description"))
                .numberOfStars(rs.getInt("number_of_stars"))
                .checkIn(rs.getInt("check_in"))
                .cleanliness(rs.getInt("cleanliness"))
                .communication(rs.getInt("communication"))
                .precision(rs.getInt("precision"))
                .location(rs.getInt("location"))
                .value(rs.getInt("value"))
                .userId(rs.getInt("user_id"))
                .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                .build();
    }
}

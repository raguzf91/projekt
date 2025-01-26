package hr.fina.student.projekt.mapper;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import hr.fina.student.projekt.entity.User;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class UserRowMapper implements RowMapper<User>{

    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
       
        
        return User.builder()
        .id(rs.getInt("id"))
        .firstName(rs.getString("first_name"))
        .lastName(rs.getString("last_name"))
        .email(rs.getString("email"))
        .password(rs.getString("password"))
        .gender(rs.getString("user_gender"))
        .phoneNumber(rs.getString("phone_number"))
        .profilePhoto(rs.getString("profile_photo"))
        .bio(rs.getString("bio"))
        .dateOfBirth(rs.getDate("date_of_birth"))
        .speaksLanguages(rs.getArray("speaks_languages") != null ? (String[]) rs.getArray("speaks_languages").getArray() : new String[0]) // Handle null
        .responseRate(rs.getInt("response_rate"))
        .role(null) // TODO
        .averageRating(rs.getDouble("average_rating"))
        .city(rs.getString("city"))
        .country(rs.getString("country"))
        .enabled(rs.getBoolean("enabled"))
        .accountLocked(rs.getBoolean("account_locked"))
        .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
        .updatedAt(rs.getTimestamp("updated_at").toLocalDateTime())
        .build();
    }
    
}

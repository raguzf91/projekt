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
        .userGender(rs.getString("user_gender"))
        .phoneNumber(rs.getString("phone_number"))
        .profilePhoto(rs.getString("profile_photo"))
        .bio(rs.getString("bio"))
        .dateOfBirth(rs.getDate("date_of_birth"))
        //.speaksLanguages((String[]) rs.getArray("speaks_languages").getArray()) // TODO
        .responseRate(rs.getInt("response_rate"))
        .enabled(rs.getBoolean("enabled"))
        .accountLocked(rs.getBoolean("account_locked"))
        .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
        .updatedAt(rs.getTimestamp("updated_at").toLocalDateTime())
        .build();
    }
    
}

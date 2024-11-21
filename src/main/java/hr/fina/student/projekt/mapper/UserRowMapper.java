package hr.fina.student.projekt.mapper;


import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;
import hr.fina.student.projekt.entity.User;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class UserRowMapper implements RowMapper<User>{

    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        Array sqlArray = rs.getArray("languages");
    List<String> languages = sqlArray != null 
        ? List.of((String[]) sqlArray.getArray()) 
        : List.of();
        
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
        .languages(languages) // TODO
        .responseRate(rs.getDouble("response_rate"))
        .profilePhoto(rs.getString("profile_photo"))
        .enabled(rs.getBoolean("enabled"))
        .accountLocked(rs.getBoolean("account_locked"))
        .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
        .updatedAt(rs.getTimestamp("updated_at").toLocalDateTime())
        .build();
    }
    
}

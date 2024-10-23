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
        .gender(rs.getString("gender"))
        .phoneNumber(rs.getString("phone_number"))
        .profilePhoto(rs.getString("profile_photo"))
        .bio(rs.getString("bio"))
        .dateOfBirth(rs.getInt("date_of_birth"))
        .languages(rs.getArray("languages")) // TODO
        .responseRate(rs.getDouble("response_rate"))
        .profilePhoto(rs.getString("profile_photo"))
        .enabled(rs.getBoolean("enabled"))
        .isAccountLocked(rs.getBoolean("account_locked"))
        .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
        .updatedAt(rs.getTimestamp("updated_at").toLocalDateTime())
        .build();
    }
    
}

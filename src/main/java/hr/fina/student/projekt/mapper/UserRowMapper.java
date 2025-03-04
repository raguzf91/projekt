package hr.fina.student.projekt.mapper;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import hr.fina.student.projekt.entity.User;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@NoArgsConstructor
@Slf4j
public class UserRowMapper implements RowMapper<User> {

    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        // Log the values of the ResultSet
        log.debug("Mapping row number: {}", rowNum);
        log.debug("id: {}", rs.getInt("id"));
        log.debug("first_name: {}", rs.getString("first_name"));
        log.debug("last_name: {}", rs.getString("last_name"));
        log.debug("email: {}", rs.getString("email"));
        log.debug("password: {}", rs.getString("password"));
        log.debug("user_gender: {}", rs.getString("user_gender"));
        log.debug("phone_number: {}", rs.getString("phone_number"));
        log.debug("profile_photo: {}", rs.getString("profile_photo"));
        log.debug("bio: {}", rs.getString("bio"));
        log.debug("date_of_birth: {}", rs.getDate("date_of_birth"));
        log.debug("speaks_languages: {}", rs.getArray("speaks_languages"));
        log.debug("response_rate: {}", rs.getInt("response_rate"));
        log.debug("enabled: {}", rs.getBoolean("enabled"));
        log.debug("account_locked: {}", rs.getBoolean("account_locked"));
        log.debug("created_at: {}", rs.getTimestamp("created_at"));
        log.debug("updated_at: {}", rs.getTimestamp("updated_at"));

        // Handle speaks_languages field with detailed logging and exception handling
        String[] speaksLanguages = new String[0];
        try {
            Array speaksLanguagesArray = rs.getArray("speaks_languages");
            if (speaksLanguagesArray != null) {
                speaksLanguages = (String[]) speaksLanguagesArray.getArray();
                log.debug("speaks_languages array: {}", (Object) speaksLanguages);
            }
        } catch (SQLException e) {
            log.error("Error processing speaks_languages array", e);
        }

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
            .speaksLanguages(speaksLanguages) // Handle null
            .responseRate(rs.getInt("response_rate"))
            .enabled(rs.getBoolean("enabled"))
            .accountLocked(rs.getBoolean("account_locked"))
            .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
            .updatedAt(rs.getTimestamp("updated_at").toLocalDateTime())
            .country(rs.getString("country"))
            .city(rs.getString("city"))
            .build();
    }
}
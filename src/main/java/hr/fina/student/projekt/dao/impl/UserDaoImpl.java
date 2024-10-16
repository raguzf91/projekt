package hr.fina.student.projekt.dao.impl;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import hr.fina.student.projekt.dao.UserDao;
import hr.fina.student.projekt.entity.User;
import lombok.AllArgsConstructor;

@Repository
@AllArgsConstructor
public class UserDaoImpl implements UserDao {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void save(User user) throws DataAccessException {
        String sql = """

                INSERT INTO Users (first_name, last_name, email, password, gender, birth_date, phone_number, profile_photo, bio, created_at, updated_at) 
                VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)";

                """;

                jdbcTemplate.update(sql, user.getFirstName(), user.getLastName(), user.getEmail(), user.getPassword(), user.getGender(), user.getDateOfBirth(), user.getPhoneNumber(), user.getProfilePhoto(), "");
    }

    @Override
    public User findByEmail(String email) throws DataAccessException {
        String sql = """
                SELECT * FROM Users WHERE Users.email = ?
                """;

        return jdbcTemplate.queryForObject(sql, User.class);
        
    }

    
    

}

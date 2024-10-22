package hr.fina.student.projekt.dao.impl;

import java.util.Collection;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import hr.fina.student.projekt.dao.UserDao;
import hr.fina.student.projekt.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.Map;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import hr.fina.student.projekt.enums.*;

@Repository
@RequiredArgsConstructor
@Slf4j
public class UserDaoImpl implements UserDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final RoleDaoImpl roleRepository;
    private final BCryptPasswordEncoder encoder;
    @Override
    public void save(User user) throws DataAccessException {
        final String SAVE_USER_QUERY = "";
       // check if the email is unique in the database
       if(findByEmail(user.getEmail().trim().toLowerCase()) != null) {
            throw new RuntimeException("Email already in use");
       }
       // save new user
       try {
        KeyHolder holder = new GeneratedKeyHolder();
        SqlParameterSource params = getSqlParameterSource(user);
        jdbcTemplate.update(SAVE_USER_QUERY, params, holder);

        // podstavi id korisniku iz holdera
        user.setId((Integer)holder.getKey());

        // Add and save the role to the user

        roleRepository.addRoleToUser(user.getId(), RoleType.ROLE_USER.name());

        // Send verification url

        String verificationUrl = getVerificationUrl(VerificationType.ACCOUNT.getType());
       } catch (EmptyResultDataAccessException e) {
       
       } catch (Exception e) {

       }
        // TODO: handle exception
       
       
       // Save url in the verification table
       // Send email to the user with verificaiton URL
       // Return the user
       // if errors happen, throw exception 
    }

    private String getVerificationUrl(String type) {
        // vrati url servera zbog testiranja
        
    }

    private SqlParameterSource getSqlParameterSource(User user) {
        return new MapSqlParameterSource()
            .addValue("firstName", user.getFirstName())
            .addValue("lastName", user.getLastName())
            .addValue("password", encoder.encode(user.getPassword()))
            .addValue("gender", user.getGender().name()) //!! potencijalni problem
            .addValue("dateOfBirth", user.getDateOfBirth())
            .addValue("phoneNumber", user.getPhoneNumber())
            .addValue("profilePhoto", user.getProfilePhoto());
    }

    @Override
    public User findByEmail(String email) throws DataAccessException {
        final String FIND_USER_BY_EMAIL = """
                SELECT * FROM Users WHERE Users.email = ?
                """;

        return jdbcTemplate.queryForObject(FIND_USER_BY_EMAIL, Map.of("email", email), User.class);
        
    }

    @Override
    public Collection<User> findAllUsers() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findAllUsers'");
    }

    @Override
    public void updateUser(Integer userId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateUser'");
    }

    @Override
    public Boolean deleteUser(Integer userId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteUser'");
    }

    
    

}

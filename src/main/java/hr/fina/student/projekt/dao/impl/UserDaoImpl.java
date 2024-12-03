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
import hr.fina.student.projekt.entity.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.Map;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import hr.fina.student.projekt.enums.*;
import hr.fina.student.projekt.exceptions.database.DatabaseException;
import hr.fina.student.projekt.exceptions.user.UserAlreadyExistsException;
import hr.fina.student.projekt.mapper.UserRowMapper;


@Repository
@RequiredArgsConstructor
@Slf4j
public class UserDaoImpl implements UserDao<User>, UserDetailsService {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final RoleDaoImpl roleRepository;
    private final BCryptPasswordEncoder encoder;
    @Override
    public User create(User user) throws DataAccessException {
        final String SAVE_USER_QUERY = """
        INSERT INTO users (first_name, last_name, email, password, date_of_birth, user_gender, bio, phone_number, response_rate, profile_photo, account_locked, enabled) VALUES (:firstName, :lastName, :email, :password, :dateOfBirth, :userGender, :bio, :phoneNumber, :responseRate, :profilePhoto, :accountLocked, :enabled)
        """;

        
       // check if the email is unique in the database
       if(findByEmail(user.getEmail().trim().toLowerCase()) != null) {
            throw new UserAlreadyExistsException("User with email: " + user.getEmail() + " already exists. Please try again");
       }
       // save new user
       try {
        log.info("Saving user: {"+ user.getEmail()+"}");
        KeyHolder holder = new GeneratedKeyHolder();
        SqlParameterSource params = getSqlParameterSource(user);
        user.setEnabled(false);
        user.setAccountLocked(true);
        jdbcTemplate.update(SAVE_USER_QUERY, params, holder, new String [] {"id"});

        // podstavi id korisniku iz holdera
        user.setId((Integer)holder.getKey());

        // Add and save the role    to the user

        roleRepository.addRoleToUser(user.getId(), RoleType.ROLE_USER.name());                              
           return user;
       } catch (Exception e) {
        log.error("Couldn't save user: " + e.getMessage());
        throw new DatabaseException("An error has occured. Please try again");
       }
        // TODO: handle exception
       
       
    }


    private SqlParameterSource getSqlParameterSource(User user) {
        return new MapSqlParameterSource()
            .addValue("firstName", user.getFirstName())
            .addValue("lastName", user.getLastName())
            .addValue("password", encoder.encode(user.getPassword()))
            .addValue("email", user.getEmail())
            .addValue("userGender", user.getUserGender()) 
            .addValue("dateOfBirth", user.getDateOfBirth())
            .addValue("bio", user.getBio())
            .addValue("phoneNumber", user.getPhoneNumber())
            .addValue("responseRate", user.getResponseRate())
            .addValue("profilePhoto", user.getProfilePhoto())
            .addValue("accountLocked", user.isAccountLocked())
            .addValue("enabled", user.isEnabled());
            //.addValue("speaksLanguages", user.getSpeaksLanguages());
    }

    @Override
    public User findByEmail(String email) throws DataAccessException {
        final String FIND_USER_BY_EMAIL = """
                SELECT * FROM Users WHERE email = :email
                """;; 
        try {
            User user = jdbcTemplate.queryForObject(FIND_USER_BY_EMAIL, Map.of("email", email), new UserRowMapper());
            return user;
        } catch (EmptyResultDataAccessException exception) {
            return null;
        } catch (Exception e) {
            log.error("Error finding user by email");
            throw new DatabaseException("An error occured in finding an email");
        } 
        
    }

    @Override
    public Collection<User> findAllUsers(int pageSize) {
        /*final String SELECT_ALL_USERS = """
                SELECT * FROM Users;
                """;
        try {
            List<User> users = jdbcTemplate.queryForList(SELECT_ALL_USERS, ) 
        } */
       return null;
        
    }

    @Override
    public Boolean updateUser(User user) {
        final String UPDATE_USER = """
                UPDATE Users SET account_locked = :accountLocked, enabled = :enabled WHERE id = :id
                """;
        try {
            jdbcTemplate.update(UPDATE_USER, Map.of("accountLocked", user.isAccountLocked() , "enabled", user.isEnabled(), "id", user.getId()));
            return true;
        } catch (Exception e) {
            log.error(e.getCause().toString());
            throw new DatabaseException("An error occured in updating the user");
        }
        
                        
    }

    @Override
    public Boolean deleteUser(Integer userId) {
        throw new UnsupportedOperationException("Unimplemented method 'deleteUser'");
    }

    @Override
    public User findById(Integer id) throws DataAccessException {
        final String FIND_USER_BY_ID = """
                SELECT * FROM Users WHERE id = :id
                """;
                log.info("Finding user by id: {}");
        try {
            User user = jdbcTemplate.queryForObject(FIND_USER_BY_ID, Map.of("id", id), new UserRowMapper());
            return user;
        } catch (EmptyResultDataAccessException exception) {
            return null;
        } catch (Exception e) {
            log.error("Error finding user by id");
            throw new DatabaseException("An error occured in finding the user by id");
        }
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = findByEmail(email);
        if(user == null) {
            log.error("User not found in the database");
            throw new UsernameNotFoundException("User not found in the database");
        } else {
            log.info("User found in the database: {}", email);
            return new UserPrincipal(user, roleRepository.getRoleByUserId(user.getId()));
        }
    }


    


    
    

}

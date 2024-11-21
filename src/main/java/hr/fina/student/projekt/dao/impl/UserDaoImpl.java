package hr.fina.student.projekt.dao.impl;

import java.util.Collection;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
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
import hr.fina.student.projekt.exceptions.ApiException;
import java.util.UUID;
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
        INSERT INTO users (first_name, last_name, email, password, date_of_birth, user_gender, phone_number, profile_photo) VALUES (:firstName, :lastName, :email, :password, :dateOfBirth, :gender, :phoneNumber, :profilePhoto)
        """;

        final String INSERT_ACCOUNT_VERIFICATION_URL = """ 
            INSERT INTO AccountVerifications (user_id, url) VALUES (:userId, :url)
            """;
       // check if the email is unique in the database
       if(findByEmail(user.getEmail().trim().toLowerCase()) != null) {
            throw new ApiException("Email already in use");
       }
       // save new user
       try {
        KeyHolder holder = new GeneratedKeyHolder();
        SqlParameterSource params = getSqlParameterSource(user);
        jdbcTemplate.update(SAVE_USER_QUERY, params, holder, new String [] {"id"});

        // podstavi id korisniku iz holdera
        user.setId((Integer)holder.getKey());

        // Add and save the role to the user

        roleRepository.addRoleToUser(user.getId(), RoleType.ROLE_USER.name());

        // Send verification url

        String verificationUrl = getVerificationUrl(VerificationType.ACCOUNT.getType());

        jdbcTemplate.update(INSERT_ACCOUNT_VERIFICATION_URL, Map.of("userId", user.getId(), "url", verificationUrl));
        user.setEnabled(false);
        user.setAccountLocked(true);
        return user;
        //sendEmail()TODO imoplementiraj ovo
        
       // Save url in the verification table
       // Send email to the user with verificaiton URL
       // Return the user
       // if errors happen, throw exception 
       } catch (EmptyResultDataAccessException e) {
        throw new RuntimeException("Couldn't save the user by: " + user.getId());
       } catch (Exception e) {
        log.error(e.getMessage());
        throw new ApiException("An error has occured. Please try again");
       }
        // TODO: handle exception
       
       
    }

    private String getVerificationUrl(String type) {
        // vrati url servera zbog testiranja
        String key = UUID.randomUUID().toString();
        return ServletUriComponentsBuilder.fromCurrentContextPath().path("/user/verify/" + type + "/" + key).toUriString();

        
    }

    private SqlParameterSource getSqlParameterSource(User user) {
        return new MapSqlParameterSource()
            .addValue("firstName", user.getFirstName())
            .addValue("lastName", user.getLastName())
            .addValue("password", encoder.encode(user.getPassword()))
            .addValue("email", user.getEmail())
            .addValue("gender", user.getGender()) 
            .addValue("dateOfBirth", user.getDateOfBirth())
            .addValue("phoneNumber", user.getPhoneNumber())
            .addValue("profilePhoto", user.getProfilePhoto());
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
            log.error(e.getMessage());
            throw new ApiException("An error occured in finding an email");
        } 
        
    }

    @Override
    public Collection<User> findAllUsers(int pageSize) {
        /*final String SELECT_ALL_USERS = """
                SELECT * FROM Users;
                """;
        try {
            List<User> users = jdbcTemplate.queryForList(SELECT_ALL_USERS, ) // TODO
        } */
       return null;
        
    }

    @Override
    public User updateUser(Integer userId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateUser'");
    }

    @Override
    public Boolean deleteUser(Integer userId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteUser'");
    }

    @Override
    public User findById(Integer id) throws DataAccessException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findById'");
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

package hr.fina.student.projekt.dao.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import hr.fina.student.projekt.dao.TokenDao;
import hr.fina.student.projekt.mapper.UserRowMapper;
import hr.fina.student.projekt.entity.Token;
import hr.fina.student.projekt.entity.User;
import hr.fina.student.projekt.exceptions.database.DatabaseException;
import hr.fina.student.projekt.mapper.TokenRowMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
@RequiredArgsConstructor
@Slf4j
public class TokenDaoImpl implements TokenDao {
    private final NamedParameterJdbcTemplate jdbcTemplate;
    @Override
    public void saveActivationToken(Token activationToken) {
        try {
            log.info("Saving activation token for user");
            final String SAVE_TOKEN = "INSERT INTO ActivationTokens (key, user_id, created_at, expires_at, type) VALUES (:key, :userId, :createdAt, :expiresAt, :type)";
    
            jdbcTemplate.update(SAVE_TOKEN, Map.of("key", activationToken.getKey(), "userId", activationToken.getUser().getId(), "createdAt", activationToken.getCreatedAt(), "expiresAt", activationToken.getExpiresAt(), "type", activationToken.getType()));
        } catch (Exception e) {
            log.error("Error saving activation token");
            throw new DatabaseException("An error has occured in saving token");
        }
       
    }


    @Override
    public Token findByKey(String key) {
        final String FIND_TOKEN_BY_KEY = """
                SELECT * FROM ActivationTokens WHERE key = :key
                """; 
        try {
            Token token = jdbcTemplate.queryForObject(FIND_TOKEN_BY_KEY, Map.of("key", key), new TokenRowMapper());
            return token;
        } catch (EmptyResultDataAccessException exception) {
            return null;
        } catch (Exception e) {
            log.error(e.getCause().toString());
            throw new DatabaseException("An error has occured in finding token");
        } 
    }


    @Override
    public Boolean updateActivationToken(Token activationToken) {
        final String UPDATE_TOKEN = """
                UPDATE ActivationTokens SET confirmed_at = :confirmedAt WHERE key = :key
                """;
        try {
            jdbcTemplate.update(UPDATE_TOKEN, Map.of("confirmedAt", activationToken.getConfirmedAt(), "key", activationToken.getKey()));
            return true;
        } catch (Exception e) {
            log.error("Error updating activation token");
            throw new DatabaseException("An error has occured in updating token");
        }
    }

    //TODO REFAKTORIZIRATI
    
   
       
            
    @Override
    public User findUserByKey(String key) {
        final String FIND_USER_BY_KEY = """
                SELECT * FROM Users WHERE id = (SELECT user_id FROM ActivationTokens WHERE key = :key)
                """;
        try {
            User userByKey = jdbcTemplate.queryForObject(FIND_USER_BY_KEY, Map.of("key", key), new UserRowMapper());
            
            return userByKey;
        } catch (EmptyResultDataAccessException exception) {
            return null;
        } catch (Exception e) {
            log.error("Error finding user by key" + e.getCause().toString());
            throw new DatabaseException("An error has occured in finding the user by key");
        }
    }

    public Set<Token> findTokensByUserId(int userId) {
        final String FIND_TOKENS_BY_USER_ID = """
                SELECT * FROM ActivationTokens WHERE user_id = :userId
                """;
        try {
            List<Token> tokens = jdbcTemplate.query(FIND_TOKENS_BY_USER_ID, Map.of("userId", userId), new TokenRowMapper());
            return new HashSet<>(tokens);
        } catch (Exception e) {
            log.error("Error finding tokens by user_id", e);
            return null;
        }
    }


    @Override
    public void deleteAllTokensByUserId(int userId) {
        final String DELETE_TOKENS_BY_USER_ID = """
                DELETE FROM ActivationTokens WHERE user_id = :userId
                """;
                try {
                    jdbcTemplate.update(DELETE_TOKENS_BY_USER_ID, Map.of("userId", userId));
                } catch (Exception e) {
                    log.error("Error finding tokens by user_id" + e.getMessage());
                    throw new DatabaseException("An error has occurred in deleting tokens by user_id");
                }
    }
    
}

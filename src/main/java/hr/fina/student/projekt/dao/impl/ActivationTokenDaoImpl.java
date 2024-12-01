package hr.fina.student.projekt.dao.impl;
import java.util.Map;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import hr.fina.student.projekt.dao.ActivationTokenDao;
import hr.fina.student.projekt.entity.ActivationToken;
import hr.fina.student.projekt.exceptions.database.DatabaseException;
import hr.fina.student.projekt.mapper.TokenRowMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ActivationTokenDaoImpl implements ActivationTokenDao {
    private final NamedParameterJdbcTemplate jdbcTemplate;
    @Override
    public void saveActivationToken(ActivationToken activationToken) {
        try {
            log.info("Saving activation token for user");
            final String SAVE_TOKEN = "INSERT INTO ActivationTokens (key, user_id, created_at, expires_at) VALUES (:key, :userId, :createdAt, :expiresAt)";
    
            jdbcTemplate.update(SAVE_TOKEN, Map.of("key", activationToken.getKey(), "userId", activationToken.getUser().getId(), "createdAt", activationToken.getCreatedAt(), "expiresAt", activationToken.getExpiresAt()));
        } catch (Exception e) {
            log.error("Error saving activation token");
            throw new DatabaseException("An error has occured in saving token");
        }
       
    }


    @Override
    public ActivationToken findByKey(String key) {
        final String FIND_TOKEN_BY_KEY = """
                SELECT * FROM ActivationTokens WHERE key = :key
                """; 
        try {
            ActivationToken token = jdbcTemplate.queryForObject(FIND_TOKEN_BY_KEY, Map.of("key", key), new TokenRowMapper());
            return token;
        } catch (EmptyResultDataAccessException exception) {
            return null;
        } catch (Exception e) {
            log.error(e.getCause().toString());
            throw new DatabaseException("An error has occured in finding token");
        } 
    }


    @Override
    public Boolean updateActivationToken(ActivationToken activationToken) {
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
    
}

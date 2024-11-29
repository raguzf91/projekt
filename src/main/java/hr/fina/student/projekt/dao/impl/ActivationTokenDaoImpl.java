package hr.fina.student.projekt.dao.impl;

import java.util.Map;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import hr.fina.student.projekt.dao.ActivationTokenDao;
import hr.fina.student.projekt.entity.ActivationToken;
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
    
            jdbcTemplate.update(SAVE_TOKEN, Map.of("key", activationToken.getKey(), "userId)", activationToken.getUser().getId(), "createdAt", activationToken.getCreatedAt(), "expiresAt", activationToken.getExpiresAt()));
        } catch (Exception e) {
            log.error("Error saving activation token");
            e.getCause().toString();
        }
       
    }
    
}

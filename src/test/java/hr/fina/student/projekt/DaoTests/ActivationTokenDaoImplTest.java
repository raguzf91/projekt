package hr.fina.student.projekt.DaoTests;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import hr.fina.student.projekt.dao.impl.TokenDaoImpl;
import hr.fina.student.projekt.entity.Token;
import hr.fina.student.projekt.entity.User;

public class ActivationTokenDaoImplTest {
    @Mock
    private NamedParameterJdbcTemplate jdbcTemplate;

    @InjectMocks
    private TokenDaoImpl activationTokenDao;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSaveActivationToken_Success() {
        Token token = new Token();
        token.setKey("testKey");
        User user = new User();
        user.setId(1);
        token.setUser(user);
        token.setCreatedAt(LocalDateTime.now());
        token.setExpiresAt(LocalDateTime.now().plusDays(1));

        doNothing().when(jdbcTemplate).update(anyString(), any(Map.class));

        assertDoesNotThrow(() -> activationTokenDao.saveActivationToken(token));
        verify(jdbcTemplate, times(1)).update(anyString(), any(Map.class));
    }

    @Test
    public void testSaveActivationToken_Exception() {
        Token token = new Token();
        token.setKey("testKey");
        User user = new User();
        user.setId(1);
        token.setUser(user);
        token.setCreatedAt(LocalDateTime.now());
        token.setExpiresAt(LocalDateTime.now().plusDays(1));

        doThrow(new RuntimeException("Database error")).when(jdbcTemplate).update(anyString(), any(Map.class));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> activationTokenDao.saveActivationToken(token));
        assertEquals("Database error", exception.getMessage());
        verify(jdbcTemplate, times(1)).update(anyString(), any(Map.class));
    }
}

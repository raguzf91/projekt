package hr.fina.student.projekt.service;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReservationCleanupService {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Scheduled(cron = "0 */5 * * * *") 
    public void invalidateExpiredReservations() {
        final String SQL = """
            UPDATE reservations
            SET valid = false
            WHERE reserved_until < CURRENT_TIMESTAMP
              AND canceled = false
            """;
        jdbcTemplate.getJdbcTemplate().update(SQL);
    }
}

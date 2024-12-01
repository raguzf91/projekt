package hr.fina.student.projekt.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

import hr.fina.student.projekt.entity.ActivationToken;
import hr.fina.student.projekt.entity.User;

public class TokenRowMapper implements RowMapper<ActivationToken> {
    

    @Override
    public ActivationToken mapRow(ResultSet rs, int rowNum) throws SQLException {
        return ActivationToken.builder()
        .id(rs.getInt("id"))
        .key(rs.getString("key"))
        .user(User.builder().id(rs.getInt("user_id")).build())
        .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
        .expiresAt(rs.getTimestamp("expires_at").toLocalDateTime())
        .updatedAt(rs.getTimestamp("updated_at").toLocalDateTime())
        .confirmedAt(rs.getTimestamp("confirmed_at") != null ? rs.getTimestamp("confirmed_at").toLocalDateTime() : null)
        .build();
    }
}

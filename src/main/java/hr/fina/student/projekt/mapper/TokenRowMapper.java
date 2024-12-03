package hr.fina.student.projekt.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

import hr.fina.student.projekt.entity.Token;
import hr.fina.student.projekt.entity.User;

public class TokenRowMapper implements RowMapper<Token> {
    

    @Override
    public Token mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Token.builder()
        .id(rs.getInt("id"))
        .key(rs.getString("key"))
        .user(User.builder().id(rs.getInt("user_id")).build())
        .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
        .expiresAt(rs.getTimestamp("expires_at").toLocalDateTime())
        .updatedAt(rs.getTimestamp("updated_at").toLocalDateTime())
        .confirmedAt(rs.getTimestamp("confirmed_at") != null ? rs.getTimestamp("confirmed_at").toLocalDateTime() : null)
        .type(rs.getString("type")) 
        .build();
    }
}

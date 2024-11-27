package hr.fina.student.projekt.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import hr.fina.student.projekt.entity.Role;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class RoleRowMapper implements RowMapper<Role>{

    @Override
    public Role mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Role.builder()
        .id(rs.getInt("id"))
        .name(rs.getString("name"))
        .permission(rs.getString("permissions"))
        .build();
    
    }
}

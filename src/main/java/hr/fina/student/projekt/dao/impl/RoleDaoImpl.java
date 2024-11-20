package hr.fina.student.projekt.dao.impl;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import hr.fina.student.projekt.dao.RoleDao;
import hr.fina.student.projekt.entity.Role;
import hr.fina.student.projekt.exceptions.ApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.Map;
import hr.fina.student.projekt.mapper.RoleRowMapper;

@Slf4j
@RequiredArgsConstructor
@Repository
public class RoleDaoImpl implements RoleDao{
private final NamedParameterJdbcTemplate jdbc;
    @Override
    public void addRoleToUser(Integer userId, String roleName) {
        final String ADD_ROLE_TO_USER = """
                INSERT INTO UserRoles (user_id, role_id) VALUES (:userId, :roleId)
                """;
        final String SELECT_ROLE_BY_NAME = """
                SELECT * FROM Roles where name = :name
                """;
        log.info("Adding role {} to user id: {}", roleName, userId);
        try {
            Role role = jdbc.queryForObject(SELECT_ROLE_BY_NAME, Map.of("name", roleName), new RoleRowMapper());
            jdbc.update(ADD_ROLE_TO_USER, Map.of("userId", userId, "roleId", role.getId()));
        } catch (EmptyResultDataAccessException e) {
            throw new RuntimeException("Error fetching role by name:" + roleName);
        } catch (Exception e) {
            log.error("Error adding role to the User");
            throw new ApiException("An error has occured");
        }
        
    }

    @Override
    public Role getRoleByUserId(Integer userId) {
        log.info("Fetching role for user id: {} ", userId);
        final String SELECT_ROLE_BY_USER_ID = """
                SELECT r.id, r.name, r.permission 
                FROM Roles r 
                JOIN UserRoles ur ON ur.role_id = r.id 
                JOIN Users u ON u.id = ur.user_id 
                WHERE u.id = :id    
                """;
        try {
            return jdbc.queryForObject(SELECT_ROLE_BY_USER_ID, Map.of("id", userId), new RoleRowMapper());
        } catch(EmptyResultDataAccessException e) {
            throw new RuntimeException("No role found for user with id: " + userId);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ApiException("An error has occured");
        }
    }

    @Override
    public Role getRoleByUserEmail(String email) {
        return null;
    }

    @Override
    public void updateUserRole(Integer userId, String roleName) {
        
        log.info("Updating role for user id: {}", userId);
        final String SELECT_ROLE_BY_NAME = """
                SELECT * FROM Roles where name = :name
                """;

        final String UPDATE_USER_ROLE = """
                UPDATE UserRoles SET role_id = :roleId WHERE user_id = :userId
                """;
        try {
            Role role = jdbc.queryForObject(SELECT_ROLE_BY_NAME, Map.of("name", roleName), new RoleRowMapper());
            jdbc.update(UPDATE_USER_ROLE, Map.of("roleId", role.getId(), "userId", userId));
        } catch (EmptyResultDataAccessException exception) {
            throw new ApiException("No role found by name: " + roleName);
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ApiException("An error occurred. Please try again.");
        }
    }
    }

  


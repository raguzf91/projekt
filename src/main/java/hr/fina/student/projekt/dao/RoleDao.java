package hr.fina.student.projekt.dao;
import hr.fina.student.projekt.entity.Role;

public interface RoleDao {
    
    void addRoleToUser(Integer userId, String roleName);
    Role getRoleByUserId(Integer userId);
    Role getRoleByUserEmail(String email);
    void updateUserRole(Integer userId, String roleName);
}

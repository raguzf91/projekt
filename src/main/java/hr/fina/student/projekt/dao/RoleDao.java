package hr.fina.student.projekt.dao;

public interface RoleDao {
    
    boolean addRoleToUser(Integer userId, String roleName);
}

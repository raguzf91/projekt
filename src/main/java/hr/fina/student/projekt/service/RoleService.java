package hr.fina.student.projekt.service;

import hr.fina.student.projekt.entity.Role;

public interface RoleService {
    
    void addRoleToUser(Integer userId, String roleName);
    Role getRoleByUserId(Integer userId);
    Role getRoleByUserEmail(String email);
}

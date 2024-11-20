package hr.fina.student.projekt.service.impl;

import org.springframework.stereotype.Service;

import hr.fina.student.projekt.dao.RoleDao;
import hr.fina.student.projekt.entity.Role;
import hr.fina.student.projekt.service.RoleService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleDao roleRepository;

    @Override
    public void addRoleToUser(Integer userId, String roleName) {
        roleRepository.addRoleToUser(userId, roleName);
    }

    @Override
    public Role getRoleByUserId(Integer userId) {
        return roleRepository.getRoleByUserId(userId);
    }

    @Override
    public Role getRoleByUserEmail(String email) {
        return roleRepository.getRoleByUserEmail(email);
    }
    
}

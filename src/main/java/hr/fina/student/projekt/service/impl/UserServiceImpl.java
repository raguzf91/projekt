package hr.fina.student.projekt.service.impl;

import hr.fina.student.projekt.dao.RoleDao;
import hr.fina.student.projekt.dao.UserDao;
import hr.fina.student.projekt.entity.User;
import hr.fina.student.projekt.dto.UserDTO;
import static hr.fina.student.projekt.mapper.UserDTOMapper.*;
import org.springframework.stereotype.Service;
import hr.fina.student.projekt.service.UserService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserDao<User> userRepository;
    private final RoleDao roleRepository;

    @Override
    public User createUser(User user) {
        return userRepository.create(user);
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    private UserDTO mapToUserDTO(User user) {
        return fromUser(user, roleRepository.getRoleByUserId(user.getId()));
    }
}

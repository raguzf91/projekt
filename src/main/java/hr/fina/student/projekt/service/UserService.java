package hr.fina.student.projekt.service;
import hr.fina.student.projekt.dto.UserDTO;
import hr.fina.student.projekt.entity.User;

public interface UserService {
    User createUser(User user);
    User findUserByEmail(String email);
    
}

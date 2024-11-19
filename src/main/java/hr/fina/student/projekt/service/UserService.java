package hr.fina.student.projekt.service;
import hr.fina.student.projekt.dto.UserDTO;
import hr.fina.student.projekt.entity.User;

public interface UserService {
    UserDTO createUser(User user);
    UserDTO findUserByEmail(String email);
    
}

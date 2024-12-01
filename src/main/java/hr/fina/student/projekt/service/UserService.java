package hr.fina.student.projekt.service;
import hr.fina.student.projekt.dto.UserDTO;
import hr.fina.student.projekt.entity.User;
import jakarta.mail.MessagingException;

public interface UserService {
    User createUser(User user);
    User findUserByEmail(String email);
    User findUserById(Integer id);
    void activateAccount(String key) throws MessagingException;
    void sendVerificationEmail(User user) throws MessagingException;
}

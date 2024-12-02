package hr.fina.student.projekt.service;
import hr.fina.student.projekt.dto.UserDTO;
import hr.fina.student.projekt.entity.User;
import jakarta.mail.MessagingException;

public interface UserService {
    User createUser(User user);
    User findUserByEmail(String email);
    User findUserById(Integer id);
    void activateAccount(String key) throws MessagingException;
    void sendEmail(User user, String url, String  verificationType) throws MessagingException;
    User verifyCode(String email, String code);
}

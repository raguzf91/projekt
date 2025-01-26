package hr.fina.student.projekt.service;
import java.util.List;

import hr.fina.student.projekt.dto.UserDTO;
import hr.fina.student.projekt.entity.Review;
import hr.fina.student.projekt.entity.User;
import jakarta.mail.MessagingException;

public interface UserService {
    User createUser(User user);
    User findUserByEmail(String email);
    User findUserById(Integer id);
    void activateAccount(String email, String key) throws MessagingException;
    void sendEmail(User user, String url, String  verificationType) throws MessagingException;
    void verifyAccount(String email, String code);
    Double getAverageRatingScore(Integer id);
    Integer getNumberOfReviews(Integer id);
    List<Review> getReviews(Integer id);
}

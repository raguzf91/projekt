package hr.fina.student.projekt.dao;

import java.util.Set;
import hr.fina.student.projekt.entity.Token;
import hr.fina.student.projekt.entity.User;


public interface TokenDao {
    void saveActivationToken(Token activationToken);
    Token findByKey(String key);
    Boolean updateActivationToken(Token activationToken);
    User findUserByKey(String key);
    Set<Token> findTokensByUserId(int userId);
    void deleteAllTokensByUserId(int userId);
}

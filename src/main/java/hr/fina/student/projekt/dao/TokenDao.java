package hr.fina.student.projekt.dao;

import hr.fina.student.projekt.entity.Token;
import hr.fina.student.projekt.entity.User;
import hr.fina.student.projekt.exceptions.key.InvalidKeyException;

public interface TokenDao {
    void saveActivationToken(Token activationToken);
    Token findByKey(String key);
    Boolean updateActivationToken(Token activationToken);
    User findUserByKey(String key);
}

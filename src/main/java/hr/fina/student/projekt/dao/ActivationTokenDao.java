package hr.fina.student.projekt.dao;

import hr.fina.student.projekt.entity.ActivationToken;

public interface ActivationTokenDao {
    void saveActivationToken(ActivationToken activationToken);
    ActivationToken findByKey(String key);
    Boolean updateActivationToken(ActivationToken activationToken);
}

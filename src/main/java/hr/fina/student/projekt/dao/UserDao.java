package hr.fina.student.projekt.dao;

import org.springframework.dao.DataAccessException;
import hr.fina.student.projekt.entity.User;

public interface UserDao {
    
    public void save(User user) throws DataAccessException;
    public User findByEmail(String email) throws DataAccessException;
}

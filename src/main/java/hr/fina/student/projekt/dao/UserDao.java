package hr.fina.student.projekt.dao;

import org.springframework.dao.DataAccessException;
import hr.fina.student.projekt.entity.User;
import java.util.Collection;

public interface UserDao {
    
    void save(User user) throws DataAccessException;
    Collection<User> findAllUsers(int pageSize); // TODO dodati paging
    User findByEmail(String email) throws DataAccessException;
    User findById(Integer id) throws DataAccessException;
    void updateUser(Integer userId);
    Boolean deleteUser(Integer userId);
}

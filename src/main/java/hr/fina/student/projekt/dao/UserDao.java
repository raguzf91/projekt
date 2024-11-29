package hr.fina.student.projekt.dao;

import org.springframework.dao.DataAccessException;
import hr.fina.student.projekt.entity.User;
import java.util.Collection;

public interface    UserDao<T extends User> {
    
    T create(T user) throws DataAccessException;
    Collection<T> findAllUsers(int pageSize); // TODO dodati paging
    T findByEmail(String email) throws DataAccessException;
    T findById(Integer id) throws DataAccessException;
    T updateUser(Integer userId);
    Boolean deleteUser(Integer userId);
}

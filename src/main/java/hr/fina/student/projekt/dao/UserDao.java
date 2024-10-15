package hr.fina.student.projekt.dao;

import java.util.Optional;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import hr.fina.student.projekt.entity.User;


public interface UserDao extends CrudRepository<User, Integer>{

    @Query("SELECT * FROM Users WHERE email = :email")
    Optional<User> findByEmail(String email);
    

}

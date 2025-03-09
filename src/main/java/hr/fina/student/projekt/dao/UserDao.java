package hr.fina.student.projekt.dao;

import org.springframework.dao.DataAccessException;

import hr.fina.student.projekt.entity.Listing;
import hr.fina.student.projekt.entity.Reservation;
import hr.fina.student.projekt.entity.Review;
import hr.fina.student.projekt.entity.Token;
import hr.fina.student.projekt.entity.User;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface    UserDao<T extends User> {
    
    T create(T user) throws DataAccessException;
    Collection<T> findAllUsers(int pageSize); // TODO dodati paging
    T findByEmail(String email) throws DataAccessException;
    T findById(Integer id) throws DataAccessException;
    Boolean enableUser(User user);
    Boolean updateUser(User user);
    Boolean deleteUser(Integer userId);
    Integer getNumberOfReviews(Integer id);
    List<Review> findAllReviews(Integer id);
    Boolean likeListing(Integer id, Integer listingId);
    Boolean isListingLiked(Integer userId, Integer listingId);
    
    
}

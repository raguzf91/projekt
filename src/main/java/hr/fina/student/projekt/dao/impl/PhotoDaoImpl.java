package hr.fina.student.projekt.dao.impl;

import hr.fina.student.projekt.dao.PhotoDao;
import hr.fina.student.projekt.entity.Photo;
import hr.fina.student.projekt.exceptions.database.DatabaseException;
import hr.fina.student.projekt.mapper.PhotoRowMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.Map;


@Repository
@RequiredArgsConstructor
@Slf4j
public class PhotoDaoImpl implements PhotoDao {
    private final NamedParameterJdbcTemplate jdbc;

    @Override
    public void savePhoto(String photoUrl, int listingId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public String findPhotoByListingId(int listingId) {
        return "";
    }

    @Override
    public Photo findPhotoById(int photoId) {
        final String FIND_PHOTO_BY_ID = """
                SELECT * FROM photos WHERE id = :id
                """;
        try {
            return jdbc.queryForObject(FIND_PHOTO_BY_ID, Map.of("id", photoId), new PhotoRowMapper());
        } catch (Exception e) {
            log.error("Error fetching photo for id {}", photoId, e.getCause());
            throw new DatabaseException("An error has occurred in fetching photo for id: " + photoId);
        }
    }
}

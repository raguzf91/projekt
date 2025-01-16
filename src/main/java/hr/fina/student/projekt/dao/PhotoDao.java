package hr.fina.student.projekt.dao;

import hr.fina.student.projekt.entity.Photo;

public interface PhotoDao {
    void savePhoto(String photoUrl, int listingId);
    String findPhotoByListingId(int listingId);
    Photo findPhotoById(int photoId);
}

package hr.fina.student.projekt.dao;

import hr.fina.student.projekt.entity.Amenity;

import java.util.List;

public interface AmenityDao {
    List<Amenity> findAllAmenities();
    Amenity findAmenitiesByDescription(String description);
}

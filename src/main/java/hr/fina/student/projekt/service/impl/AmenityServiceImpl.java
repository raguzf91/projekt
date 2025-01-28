package hr.fina.student.projekt.service.impl;

import hr.fina.student.projekt.service.AmenityService;
import hr.fina.student.projekt.dao.AmenityDao;
import hr.fina.student.projekt.entity.Amenity;

import java.util.List;

public class AmenityServiceImpl implements AmenityService {
    private final AmenityDao amenityDao;

    @Override
    public List<Amenity> getAllAmenities() {
        return amenityDao.findAllAmenities();
    }


}

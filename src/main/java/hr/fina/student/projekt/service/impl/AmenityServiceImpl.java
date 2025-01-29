package hr.fina.student.projekt.service.impl;

import hr.fina.student.projekt.service.AmenityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import hr.fina.student.projekt.dao.AmenityDao;
import hr.fina.student.projekt.entity.Amenity;
import java.util.List;

import org.springframework.stereotype.Service;
@Service
@Slf4j
@RequiredArgsConstructor
public class AmenityServiceImpl implements AmenityService {
    private final AmenityDao amenityDao;

    @Override
    public List<Amenity> getAllAmenities() {
        return amenityDao.findAllAmenities();
    }

    @Override
    public Amenity findAmenitiesByDescription(String description) {
        return amenityDao.findAmenitiesByDescription(description);
    }


}

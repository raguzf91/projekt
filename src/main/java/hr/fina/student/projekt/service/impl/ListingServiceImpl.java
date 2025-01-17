package hr.fina.student.projekt.service.impl;

import hr.fina.student.projekt.dao.ListingDao;
import hr.fina.student.projekt.entity.Listing;
import hr.fina.student.projekt.service.ListingService;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ListingServiceImpl implements ListingService {
    private final ListingDao listingDao;

    @Override
    public List<Listing> getAllListings() {
        return listingDao.findAllListings();
    }

    @Override
    public Listing getListing(Integer id) {
        return listingDao.findListing(id);
    }

    @Override
    public List<Listing> getListingsByCategory(String category) {
        return listingDao.findListingByCategory(category);
    }
}

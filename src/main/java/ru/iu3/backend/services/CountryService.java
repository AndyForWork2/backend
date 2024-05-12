package ru.iu3.backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.iu3.backend.models.Artist;
import ru.iu3.backend.models.Country;
import ru.iu3.backend.repositories.ArtistRepository;
import ru.iu3.backend.repositories.CountryRepository;

import java.util.List;

@Service
public class CountryService {
    @Autowired
    private CountryRepository countryRepository;
    @Autowired
    private ArtistRepository artistRepository;

    public void delete(Country country){
        for (Artist artist: country.artists){
            artist.country = null;
            artistRepository.save(artist);
        }
        country.artists.clear();
        countryRepository.delete(country);
    }

    public void deleteAll(List<Country> countries){
        for (Country country: countries){
            delete(country);
        }
    }
}

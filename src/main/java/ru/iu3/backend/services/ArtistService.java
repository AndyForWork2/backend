package ru.iu3.backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.iu3.backend.models.Artist;
import ru.iu3.backend.models.Country;
import ru.iu3.backend.models.Painting;
import ru.iu3.backend.repositories.ArtistRepository;
import ru.iu3.backend.repositories.CountryRepository;
import ru.iu3.backend.repositories.PaintingRepository;

import java.util.List;

@Service
public class ArtistService {

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private PaintingRepository paintingRepository;

    public void delete(Artist artist){
        Country country =  artist.country;
        if (country != null) {
            country.artists.remove(artist);
            countryRepository.save(country);
        }
        for (Painting painting: artist.paintings){
            painting.artist = null;
            paintingRepository.save(painting);
        }
        artist.paintings.clear();
        artistRepository.delete(artist);
    }

    public void deleteAll(List<Artist> artists){
        for (Artist artist: artists) {
            Country country =  artist.country;
            if (country != null) {
                country.artists.remove(artist);
                countryRepository.save(country);
            }
            for (Painting painting : artist.paintings) {
                painting.artist = null;
                paintingRepository.save(painting);
            }
            artist.paintings.clear();
            artistRepository.delete(artist);
        }
    }
}

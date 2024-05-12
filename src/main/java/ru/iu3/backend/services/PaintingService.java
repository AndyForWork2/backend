package ru.iu3.backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.iu3.backend.models.Artist;
import ru.iu3.backend.models.Museum;
import ru.iu3.backend.models.Painting;
import ru.iu3.backend.repositories.ArtistRepository;
import ru.iu3.backend.repositories.MuseumRepository;
import ru.iu3.backend.repositories.PaintingRepository;

import java.util.List;

@Service
public class PaintingService {

    @Autowired
    private PaintingRepository paintingRepository;

    @Autowired
    private MuseumRepository museumRepository;

    @Autowired
    private ArtistRepository artistRepository;

    public void delete(Painting painting){
        Museum museum = painting.museum;
        if (museum != null){
            museum.paintings.remove(painting);
            museumRepository.save(museum);
        }
        Artist artist = painting.artist;
        if (artist != null){
            artist.paintings.remove(painting);
            artistRepository.save(artist);
        }
        painting.museum = null;
        painting.artist = null;
        paintingRepository.delete(painting);
    }

    public void deleteAll(List<Painting> paintings){
        for (Painting painting: paintings) {
            Museum museum = painting.museum;
            if (museum != null) {
                museum.paintings.remove(painting);
                museumRepository.save(museum);
            }
            Artist artist = painting.artist;
            if (artist != null) {
                artist.paintings.remove(painting);
                artistRepository.save(artist);
            }
            painting.museum = null;
            painting.artist = null;
            paintingRepository.delete(painting);
        }
    }
}

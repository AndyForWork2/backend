package ru.iu3.backend.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.iu3.backend.models.Artist;
import ru.iu3.backend.models.Country;
import ru.iu3.backend.models.User;
import ru.iu3.backend.repositories.ArtistRepository;
import ru.iu3.backend.repositories.CountryRepository;
import ru.iu3.backend.services.ArtistService;
import ru.iu3.backend.tools.DataValidationException;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/v1")
public class ArtistController {
    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private ArtistService artistService;


    @GetMapping("/artists")
    public Page<Artist> getAllArtists(@RequestParam("page") String page, @RequestParam("limit") String limit) {
        if (!page.equals("undefined") )
            return artistRepository.findAll(PageRequest.of(Integer.parseInt(page), Integer.parseInt(limit), Sort.by(Sort.Direction.ASC, "name")));
        return artistRepository.findAll(PageRequest.of(0, Integer.parseInt(limit), Sort.by(Sort.Direction.ASC, "name")));
    }


    @PostMapping("/artists")
    public ResponseEntity<Object> createArtist(@RequestBody Artist artist) throws Exception {
        try {
            Artist nc = artistRepository.save(artist);
            return new ResponseEntity<Object>(nc, HttpStatus.OK);
        }
        catch(Exception ex) {
            String error;
            if (ex.getMessage().contains("artists.name_UNIQUE"))
                throw new DataValidationException("Художник с таким именем уже существует");
            else if (ex.getMessage().contains("Column 'name' cannot be null"))
                throw new DataValidationException("Имя художника не может быть пустым");
            else
                throw new DataValidationException("Неизвестная ошибка");
        }
    }

    @PutMapping("/artists/{id}")
    public ResponseEntity<Artist> updateArtist(@PathVariable(value = "id") Long artistId,
                                                 @RequestBody Artist artistDetails) throws DataValidationException {
        Artist artist = null;
        Optional<Artist>
                cc = artistRepository.findById(artistId);
        if (cc.isPresent()) {
            artist= cc.get();
            artist.name = artistDetails.name;
            artist.age = artistDetails.age;
            artistRepository.save(artist);
            return ResponseEntity.ok(artist);
        } else {
            throw new DataValidationException("Художника с таким идентификатором не найдено");
        }
    }

    @DeleteMapping("/artists/{id}")
    public ResponseEntity<Object> deleteArtist(@PathVariable(value = "id") Long artistId) {
        Optional<Artist>
                artist = artistRepository.findById(artistId);
        Map<String, Boolean>
                resp = new HashMap<>();
        if (artist.isPresent()) {
            artistService.delete(artist.get());
            resp.put("deleted", Boolean.TRUE);
        }
        else
            resp.put("deleted", Boolean.FALSE);
        return ResponseEntity.ok(resp);
    }


    @PostMapping("/deleteartists")
    public ResponseEntity deleteArtist(@Valid @RequestBody List<Artist> artists) {
        artistService.deleteAll(artists);
        return new ResponseEntity(HttpStatus.OK);
    }


    @GetMapping("/artists/{id}")
    public ResponseEntity getArtist(@PathVariable(value = "id") Long artistId)
            throws DataValidationException {
        Artist artist = artistRepository.findById(artistId)
                .orElseThrow(()->new DataValidationException("Художника с таким индексомне существует"));
        return ResponseEntity.ok(artist);
    }

}
package ru.iu3.backend.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.iu3.backend.models.Artist;
import ru.iu3.backend.models.Painting;
import ru.iu3.backend.repositories.ArtistRepository;
import ru.iu3.backend.repositories.CountryRepository;
import ru.iu3.backend.repositories.PaintingRepository;
import ru.iu3.backend.services.ArtistService;
import ru.iu3.backend.services.PaintingService;
import ru.iu3.backend.tools.DataValidationException;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/v1")
public class PaintingController {

    @Autowired
    private PaintingRepository paintingRepository;

    @Autowired
    private PaintingService paintingService;


    @GetMapping("/paintings")
    public Page<Painting> getAllPaintings(@RequestParam("page") String page, @RequestParam("limit") String limit) {
        if (!page.equals("undefined") )
            return paintingRepository.findAll(PageRequest.of(Integer.parseInt(page), Integer.parseInt(limit), Sort.by(Sort.Direction.ASC, "name")));
        return paintingRepository.findAll(PageRequest.of(0, Integer.parseInt(limit), Sort.by(Sort.Direction.ASC, "name")));
    }


    @PostMapping("/paintings")
    public ResponseEntity<Object> createPainting(@RequestBody Painting painting) throws Exception {
        try {
            Painting nc = paintingRepository.save(painting);
            return new ResponseEntity<Object>(nc, HttpStatus.OK);
        }
        catch(Exception ex) {
            String error;
            if (ex.getMessage().contains("paintings.name_UNIQUE"))
                throw new DataValidationException("Картина с таким названием уже существует");
            else if (ex.getMessage().contains("Column 'name' cannot be null"))
                throw new DataValidationException("Название картины не может быть пустым");
            else
                throw new DataValidationException("Неизвестная ошибка");
        }
    }

    @PutMapping("/paintings/{id}")
    public ResponseEntity<Painting> updatePainting(@PathVariable(value = "id") Long paintingId,
                                               @RequestBody Painting paintingDetails) throws DataValidationException {
        Painting painting= null;
        Optional<Painting>
                cc = paintingRepository.findById(paintingId);
        if (cc.isPresent()) {
            painting= cc.get();
            painting.name = paintingDetails.name;
            painting.artist = paintingDetails.artist;
            painting.museum = paintingDetails.museum;
            paintingRepository.save(painting);
            return ResponseEntity.ok(painting);
        } else {
            throw new DataValidationException("Картины с таким индексом не найдено");
        }
    }

    @DeleteMapping("/paintings/{id}")
    public ResponseEntity<Object> deletePainting(@PathVariable(value = "id") Long paintingId) {
        Optional<Painting>
                painting = paintingRepository.findById(paintingId);
        Map<String, Boolean>
                resp = new HashMap<>();
        if (painting.isPresent()) {
            paintingService.delete(painting.get());
            resp.put("deleted", Boolean.TRUE);
        }
        else
            resp.put("deleted", Boolean.FALSE);
        return ResponseEntity.ok(resp);
    }


    @PostMapping("/deletepaintings")
    public ResponseEntity deletePainting(@Valid @RequestBody List<Painting> paintings) {
        paintingService.deleteAll(paintings);
        return new ResponseEntity(HttpStatus.OK);
    }


    @GetMapping("/paintings/{id}")
    public ResponseEntity getPainting(@PathVariable(value = "id") Long paintingId)
            throws DataValidationException {
        Painting painting= paintingRepository.findById(paintingId)
                .orElseThrow(()->new DataValidationException("Картины с таким индексом не существует"));
        return ResponseEntity.ok(painting);
    }

}

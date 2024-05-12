package ru.iu3.backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.iu3.backend.models.Country;
import ru.iu3.backend.models.Museum;
import ru.iu3.backend.repositories.MuseumRepository;
import ru.iu3.backend.services.MuseumService;
import ru.iu3.backend.tools.DataValidationException;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/v1")
public class MuseumController {

    @Autowired
    private MuseumRepository museumRepository;

    @Autowired
    private MuseumService museumService;

    @GetMapping("/museums")
    public Page<Museum> getAllCountries(@RequestParam("page") String page, @RequestParam("limit") String limit) {
        if (!page.equals("undefined") )
            return museumRepository.findAll(PageRequest.of(Integer.parseInt(page), Integer.parseInt(limit), Sort.by(Sort.Direction.ASC, "name")));
        return museumRepository.findAll(PageRequest.of(0, Integer.parseInt(limit), Sort.by(Sort.Direction.ASC, "name")));
    }


    @PostMapping("/museums")
    public ResponseEntity<Object> createMuseum(@RequestBody Museum museum)
            throws Exception {
        try {
            Museum nc = museumRepository.save(museum);
            return new ResponseEntity<Object>(nc, HttpStatus.OK);
        }
        catch(Exception ex) {
            if (ex.getMessage().contains("museums.name_UNIQUE"))
                throw  new DataValidationException("Уже существует музей с таким названием");
            else if (ex.getMessage().contains("Column 'name' cannot be null"))
                throw  new DataValidationException("Название музея пустое");
            else if (ex.getMessage().contains("Column 'location' cannot be null"))
                throw  new DataValidationException("Не указан адрес музея");
            else
                throw  new DataValidationException("Неизвестная ошибка");
        }
    }



    @PutMapping("/museums/{id}")
    public ResponseEntity<Museum> updateCountry(@PathVariable(value = "id") Long museumId,
                                                 @RequestBody Museum museumDetails) throws DataValidationException {
        Museum museum = null;
        Optional<Museum>
                cc = museumRepository.findById(museumId);
        if (cc.isPresent()) {
            museum = cc.get();
            museum.name = museumDetails.name;
            museum.location = museumDetails.location;
            museumRepository.save(museum);
            return ResponseEntity.ok(museum);
        } else {
            throw new DataValidationException("Неизвестная ошибка");
        }
    }

    @DeleteMapping("/museums/{id}")
    public ResponseEntity<Object> deleteMuseum(@PathVariable(value = "id") Long museumId) {
        Optional<Museum>
                museum = museumRepository.findById(museumId);
        Map<String, Boolean>
                resp = new HashMap<>();
        if (museum.isPresent()) {
            museumService.delete(museum.get());
            resp.put("deleted", Boolean.TRUE);
        }
        else
            resp.put("deleted", Boolean.FALSE);
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/deletemuseums")
    public ResponseEntity deleteMuseums(@Valid @RequestBody List<Museum> museums) {
        museumService.deleteAll(museums);
        return new ResponseEntity(HttpStatus.OK);
    }


    @GetMapping("/museums/{id}")
    public ResponseEntity getMuseum(@PathVariable(value = "id") Long museumId)
            throws DataValidationException {
        Museum museum = museumRepository.findById(museumId)
                .orElseThrow(()->new DataValidationException("Музея с таким индексомне существует"));
        return ResponseEntity.ok(museum);
    }
}

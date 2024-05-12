package ru.iu3.backend.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.iu3.backend.models.Country;
import ru.iu3.backend.models.Museum;
import ru.iu3.backend.models.User;
import ru.iu3.backend.repositories.MuseumRepository;
import ru.iu3.backend.repositories.UserRepository;
import ru.iu3.backend.services.UserService;
import ru.iu3.backend.tools.DataValidationException;
import ru.iu3.backend.tools.Utils;

import javax.validation.Valid;
import java.util.*;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/v1")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MuseumRepository museumRepository;

    @Autowired
    private UserService userService;


    @GetMapping("/users")
    public Page<User> getAllUsers(@RequestParam("page") String page, @RequestParam("limit") String limit) {
        if (!page.equals("undefined") )
            return userRepository.findAll(PageRequest.of(Integer.parseInt(page), Integer.parseInt(limit), Sort.by(Sort.Direction.ASC, "login")));
        return userRepository.findAll(PageRequest.of(0, Integer.parseInt(limit), Sort.by(Sort.Direction.ASC, "login")));
    }


    @PostMapping("/users")
    public ResponseEntity<Object> createUser(@RequestBody User user)
            throws Exception {
        try {
            if (user.salt == null) {
                byte[] b = new byte[32];
                new Random().nextBytes(b);
                user.salt = new String(Hex.encode(b));
            }
            User nc = userRepository.save(user);
            return new ResponseEntity<Object>(nc, HttpStatus.OK);
        } catch (Exception ex) {
            String error;
            if (ex.getMessage().contains("users.name_UNIQUE"))
                throw  new DataValidationException("Пользователь с таким именем уже существует");
            else if (ex.getMessage().contains("Column 'login' cannot be null"))
                throw new DataValidationException("Логин пользователя не может быть пустым");
            else if (ex.getMessage().contains("Column 'email' cannot be null"))
                throw new DataValidationException("Почта пользваоетля не моет быть пустой");
            else
                throw new DataValidationException("Неизвестная ошибка");
        }
    }


    @PutMapping("/users/{id}")
    public ResponseEntity updateUser(@PathVariable(value = "id") Long userId,
                                     @Valid @RequestBody User userDetails)
            throws DataValidationException
    {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new DataValidationException(" Пользователь с таким индексом не найден"));
            user.email = userDetails.email;
            String np = userDetails.np;
            if (np != null  && !np.isEmpty()) {
                byte[] b = new byte[32];
                new Random().nextBytes(b);
                String salt = new String(Hex.encode(b));
                user.password = Utils.ComputeHash(np, salt);
                user.salt = salt;
            }
            userRepository.save(user);
            return ResponseEntity.ok(user);
        }
        catch (Exception ex) {
            String error;
            if (ex.getMessage().contains("users.email_UNIQUE"))
                throw new DataValidationException("Пользователь с такой почтой уже есть в базе");
            else
                throw new DataValidationException("Неизвестная ошибка");
        }
    }


    @DeleteMapping("/users/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable(value = "id") Long userId) {
        Optional<User>
                User = userRepository.findById(userId);
        Map<String, Boolean>
                resp = new HashMap<>();
        if (User.isPresent()) {
            userService.delete(User.get());
            resp.put("deleted", Boolean.TRUE);
        } else {
            resp.put("deleted", Boolean.FALSE);
        }
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/users/{id}/addmuseums")
    public ResponseEntity<Object> addMuseums(@PathVariable(value = "id") Long userId,
                                              @RequestBody Set<Museum> museums) {
        Optional<User> uu = userRepository.findById(userId);
        int cnt = 0;
        if (uu.isPresent()) {
            User u = uu.get();
            for (Museum m : museums) {
                Optional<Museum>
                        mm = museumRepository.findById(m.id);
                if (mm.isPresent()) {
                    u.museums.add(mm.get());
                    cnt++;
                }
            }
            userRepository.save(u);
        }
        Map<String, String> response = new HashMap<>();
        response.put("count", String.valueOf(cnt));
        return ResponseEntity.ok(response);
    }



    @PostMapping("/deleteusers")
    public ResponseEntity deleteUsers(@Valid @RequestBody List<User> users) {
        userService.deleteAll(users);
        return new ResponseEntity(HttpStatus.OK);
    }


    @GetMapping("/users/{id}")
    public ResponseEntity getUser(@PathVariable(value = "id") Long userId)
            throws DataValidationException {
        User user = userRepository.findById(userId)
                .orElseThrow(()->new DataValidationException("Пользователя с таким индексомне существует"));
        return ResponseEntity.ok(user);
    }


}

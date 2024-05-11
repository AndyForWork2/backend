package ru.iu3.backend.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.iu3.backend.models.Museum;
import ru.iu3.backend.models.User;
import ru.iu3.backend.repositories.MuseumRepository;
import ru.iu3.backend.repositories.UserRepository;

import java.util.*;

@RestController
@RequestMapping("/api/v1")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MuseumRepository museumRepository;

    @GetMapping("/users")
    public List getAllUsers() {
        return userRepository.findAll();
    }


    @PostMapping("/users")
    public ResponseEntity<Object> createUser(@RequestBody User User)
            throws Exception {
        try {
            User nc = userRepository.save(User);
            return new ResponseEntity<Object>(nc, HttpStatus.OK);
        } catch (Exception ex) {
            String error;
            if (ex.getMessage().contains("museums.name_UNIQUE"))
                error = "museumAlreadyExists";
            else if (ex.getMessage().contains("Column 'login' cannot be null"))
                error = "userLoginIsEmpty";
            else if (ex.getMessage().contains("Column 'email' cannot be null"))
                error = "userEmailIsEmpty";
            else
                error = "undefinedError";
            Map<String, String>
                    map = new HashMap<>();
            map.put("error", error);
            return new ResponseEntity<Object>(map, HttpStatus.OK);
        }
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<User> updateUser(@PathVariable(value = "id") Long userId,
                                             @RequestBody User userDetails) {
        User user;
        Optional<User>
                cc = userRepository.findById(userId);
        if (cc.isPresent()) {
            user = cc.get();
            user.login = userDetails.login;
            user.email = userDetails.email;
            user.password = userDetails.password;
            user.salt = userDetails.salt;
            user.token = userDetails.token;
            user.activity = userDetails.activity;
            userRepository.save(user);
            return ResponseEntity.ok(user);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable(value = "id") Long userId) {
        Optional<User>
                User = userRepository.findById(userId);
        Map<String, Boolean>
                resp = new HashMap<>();
        if (User.isPresent()) {
            userRepository.delete(User.get());
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

}

package ru.iu3.backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.iu3.backend.models.Museum;
import ru.iu3.backend.models.User;
import ru.iu3.backend.repositories.MuseumRepository;
import ru.iu3.backend.repositories.UserRepository;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MuseumRepository museumRepository;

    public void delete(User user){
        for (Museum museum: user.museums){
            museum.users.remove(user);
            museumRepository.save(museum);
        }
        user.museums.clear();
        userRepository.delete(user);
    }

    public void deleteAll(List<User> users){
        for (User user: users) {
            for (Museum museum : user.museums) {
                museum.users.remove(user);
                museumRepository.save(museum);
            }
            user.museums.clear();
            userRepository.delete(user);
        }
    }
}

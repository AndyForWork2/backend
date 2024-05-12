package ru.iu3.backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.iu3.backend.models.Museum;
import ru.iu3.backend.models.Painting;
import ru.iu3.backend.models.User;
import ru.iu3.backend.repositories.MuseumRepository;
import ru.iu3.backend.repositories.PaintingRepository;
import ru.iu3.backend.repositories.UserRepository;

import java.util.List;

@Service
public class MuseumService {

    @Autowired
    private MuseumRepository museumRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PaintingRepository paintingRepository;



    public void delete(Museum museum){
        for (User user: museum.users){
            user.museums.remove(museum);
            userRepository.save(user);
        }
        for (Painting painting: museum.paintings){
            painting.museum = null;
            paintingRepository.save(painting);
        }
        museum.users.clear();
        museum.paintings.clear();
        museumRepository.delete(museum);
    }

    public void deleteAll(List<Museum> museums){
        for (Museum museum: museums) {
            for (User user : museum.users) {
                user.museums.remove(museum);
                userRepository.save(user);
            }
            for (Painting painting : museum.paintings) {
                painting.museum = null;
                paintingRepository.save(painting);
            }
            museum.users.clear();
            museum.paintings.clear();
            museumRepository.delete(museum);
        }
    }
}

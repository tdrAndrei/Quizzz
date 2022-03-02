package server.service;

import commons.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import server.database.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepo;

    @Autowired
    public UserService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    public Optional<User> getById(Long id) {
        if (id < 0 || !userRepo.existsById(id)) {
            return Optional.empty();
        } else {
            return Optional.of(userRepo.findById(id).get());
        }
    }

    public User insert(User user) {
        if (user == null || user.getName().isEmpty()){
            return null;
        }
        return userRepo.save(user);
    }

    public void deleteById(Long id) {
        if (id < 0 || !userRepo.existsById(id)) {
            return;
        } else {
            userRepo.deleteById(id);
        }
    }

    public Optional<User> updateById(Long id, String name) {
        Optional<User> toUpdate = getById(id);

        if (toUpdate.isEmpty()) {
            return toUpdate;
        } else {
            toUpdate.get().setName(name);
            userRepo.save(toUpdate.get());
            return toUpdate;
        }
    }
}

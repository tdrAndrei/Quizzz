package server.service;

import org.springframework.stereotype.Service;
import server.database.UserRepository;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepo;

    public UserService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    public Optional<User> getById(Long id) {
        if( id < 0 || !userRepo.existsById(id) ) {
            return Optional < User >.empty();
        } else {
            return Optional.of(userRepo.findById(id).get());
        }
    }

    public User insert(User user) {
        return userRepo.save(user);
    }

    public void deleteById(Long id) {
        if( id < 0 || !userRepo.existsById(id) ) {
            return ;
        } else {
            userRepo.deleteById(id);
        }
    }

    public Optional<User> updateById(Long id, User user) {
        Optional<User> toUpdate = getById(id);

        if (toUpdate.isEmpty()) {
            return toUpdate;
        } else {
            toUpdate.get().setName(user.getName());
            userRepo.save(toUpdate.get());
            return toUpdate;
        }

    }
}

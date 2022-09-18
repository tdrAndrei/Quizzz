package server.api;

import commons.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;



import server.service.UserService;

import java.util.List;

@RestController
@RequestMapping(path = "api/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> getUsers() {
        return userService.getAllUsers();
    }

    @PostMapping
    public ResponseEntity<User> registerNewUser(@RequestBody User user) {
        User insertedUser = userService.insert(user);
        if (insertedUser == null){
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(insertedUser);
    }

    @DeleteMapping(path = "{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable("userId") Long userId) {
        if (!userService.deleteById(userId)){
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok().build();
    }

    @PutMapping(path = "{userId}")
    public ResponseEntity<?> updateUser(@PathVariable("userId") Long userId , @RequestParam(required = false) String name) {
        if (userService.updateById(userId, name).isEmpty()){
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok().build();
    }
}
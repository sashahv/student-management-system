package pl.dfjp.students.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.dfjp.students.dto.UserDTO;
import pl.dfjp.students.entity.User;
import pl.dfjp.students.service.UserService;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }
//
//    @PostMapping
//    private User addUser(@RequestBody UserDTO userDTO){
//        return userService.createUser(userDTO);
//    }
}

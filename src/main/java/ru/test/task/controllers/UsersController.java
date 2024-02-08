package ru.test.task.controllers;


import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.test.task.models.User;
import ru.test.task.services.UserService;
import ru.test.task.util.GenerateHttpStatus;
import ru.test.task.util.UserValidator;

import java.net.URI;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UsersController {

    private final UserService userService;
    private final UserValidator userValidator;

    @Autowired
    public UsersController(UserService userService, UserValidator userValidator) {
        this.userService = userService;
        this.userValidator = userValidator;
    }


    @GetMapping
    public List<User> getAllUsers(@RequestParam(required = false) String userName) {
        System.out.println(userService.getAllUsers(userName));
        return userService.getAllUsers(userName);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable("id") int id) {
        User user = userService.getUserById(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }

    @PostMapping()
    public ResponseEntity<?> createUser(@RequestBody @Valid User user, BindingResult bindingResult) {
        userValidator.validate(user, bindingResult);
        ResponseEntity<?> response = GenerateHttpStatus.generateAnswer(user, bindingResult);
        if (response.getStatusCode().is4xxClientError()) {
            return response;
        }
        userService.saveOnlyUser(user);
        return ResponseEntity
                .created(URI.create("/api/users/" + user.getId()))
                .body(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable("id") int id, @RequestBody @Valid User user, BindingResult bindingResult) {
        user.setId(id);
        userValidator.validate(user, bindingResult);
        ResponseEntity<?> response = GenerateHttpStatus.generateAnswer(user, bindingResult);
        if (response.getStatusCode().is4xxClientError()) {
            return response;
        }
        User userToCheck = userService.update(id, user);
        if (userToCheck == null) {
            return ResponseEntity.badRequest().body(user);
        }
        return ResponseEntity.ok().body(user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteUser(@PathVariable("id") int id) {
        User user = userService.getUserById(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        userService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

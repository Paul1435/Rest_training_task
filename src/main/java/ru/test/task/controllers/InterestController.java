package ru.test.task.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.view.RedirectView;
import ru.test.task.models.Interest;
import ru.test.task.models.User;
import ru.test.task.services.InterestService;
import ru.test.task.util.GenerateHttpStatus;
import ru.test.task.util.InterestValidate;

import java.util.List;

@RestController
@RequestMapping("/api")
public class InterestController {
    private final InterestService interestService;
    private final InterestValidate interestValidate;

    @Autowired
    public InterestController(InterestService interestService, InterestValidate interestValidate) {
        this.interestService = interestService;
        this.interestValidate = interestValidate;
    }

    @GetMapping("/interests")
    public List<Interest> getAllInterests() {
        System.out.println(interestService.findAllInterests());
        return interestService.findAllInterests();
    }

    @GetMapping("/users/{id}/interests")
    public ResponseEntity<?> getAllInterests(@PathVariable(name = "id") int userId) {
        List<Interest> interests = interestService.findByUserId(userId);
        if (interests == null || interests.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(interests);
    }

    @GetMapping("/interests/{id}")
    public ResponseEntity<?> getInterestsById(@PathVariable(value = "id") int interestId) {
        Interest interest = interestService.findById(interestId);
        if (interest == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(interest);
    }

    @GetMapping("/interests/{id}/users")
    public ResponseEntity<?> getAllUsersByInterestId(@PathVariable(value = "id") int id) {
        List<User> users = interestService.findAllUserById(id);
        if (users == null || users.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(users);
    }

    @PostMapping("/users/{userId}/interests")
    public ResponseEntity<?> addInterest(@PathVariable(name = "userId") int userId, @RequestBody @Valid Interest interest) {


        List<Interest> interests = interestService.addInterestForUser(userId, interest);
        if (interests != null) {
            return ResponseEntity.ok(interests);
        }
        return ResponseEntity.badRequest().build();
    }

    @PutMapping("/interests/{id}")
    public ResponseEntity<?> updateInterest(@PathVariable(name = "id") int id, @RequestBody @Valid Interest updateInterest,
                                            BindingResult bindingResult) {
        updateInterest.setId(id);
        interestValidate.validate(updateInterest, bindingResult);
        interestService.updateInterest(id, updateInterest);
        ResponseEntity<?> response = GenerateHttpStatus.generateAnswer(updateInterest, bindingResult);
        return response;
    }

    @DeleteMapping("users/{user_id}/interests/{interest_id}")
    public ResponseEntity<HttpStatus> deleteInterestForUser(@PathVariable("user_id") int userId,
                                                            @PathVariable("interest_id") int interestId) {
        boolean isDelete = interestService.deleteInterestForUser(userId, interestId);
        if (isDelete) {
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        }
        return new ResponseEntity<>(HttpStatus.CONFLICT);
    }

    @DeleteMapping("interests/{interest_id}")
    public RedirectView deleteInterest(@PathVariable("interest_id") int interestId) {
        Interest interestToDelete = interestService.findById(interestId);
        if (interestToDelete == null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Interest with id = " + interestId + " not Found");
        }
        interestService.deleteInterest(interestId);
        return new RedirectView("/api" + "/interests", true);
    }

    @PostMapping("interests")
    public ResponseEntity<?> createInterest(@RequestBody @Valid Interest newInterest, BindingResult bindingResult) {
        interestValidate.validate(newInterest, bindingResult);
        if (bindingResult.hasErrors()) {
            return GenerateHttpStatus.generateAnswer(newInterest, bindingResult);
        }
        Interest interest = interestService.createNewInterest(newInterest);
        return ResponseEntity.ok(interest);
    }
}

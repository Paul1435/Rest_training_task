package ru.test.task.util;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.test.task.models.User;
import ru.test.task.services.UserService;

import java.util.Objects;

@Component
public class UserValidator implements Validator {

    private final UserService userService;

    @Autowired
    public UserValidator(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return User.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        User user = (User) target;
        if (userService.findByEmail(user.getEmail())
                .filter(userFromBD -> !Objects.equals(userFromBD.getId(), user.getId())).isPresent()) {
            errors.rejectValue("email", "", "This email is Already taken");
        }
        if (userService.findByUsername(user.getUserName())
                .filter(userFromBD -> !Objects.equals(userFromBD.getId(), user.getId())).isPresent()) {
            errors.rejectValue("userName", "", "This username is Already taken");
        }
        if (userService.getUserByFullname(user.getFullName())
                .filter(userFromBD -> !Objects.equals(userFromBD.getId(), user.getId())).isPresent()) {
            errors.rejectValue("fullName", "", "User with that name has already been registered");
        }
    }
}

package ru.test.task.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import ru.test.task.models.Interest;
import ru.test.task.models.User;
import ru.test.task.repositories.InterestRepository;
import ru.test.task.repositories.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final InterestRepository interestRepository;

    @Autowired
    public UserService(UserRepository userRepository, InterestRepository interestRepository) {
        this.userRepository = userRepository;
        this.interestRepository = interestRepository;
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUserNameContaining(username).stream().findFirst();
    }

    public List<User> getAllUsers(String username) {
        List<User> users = new ArrayList<>();
        if (username == null || username.isEmpty()) {
            users.addAll(userRepository.findAll());
        } else {
            users.addAll(userRepository.findByUserNameContaining(username));
        }
        return users;
    }

    public Optional<User> getUserByFullname(String fullname) {
        return userRepository.findUserByFullName(fullname);
    }


    public User getUserById(int id) {
        return userRepository.findById(id).orElse(null);
    }


    @Transactional
    public void saveOnlyUser(User user) {
        List<Interest> userInterests = user.getInterests();
        List<Interest> existingInterests = new ArrayList<>();
        for (Interest interest : userInterests) {
            Interest existingInterest = interestRepository.findInterestByLabel(interest.getLabel());
            if (existingInterest != null) {
                existingInterests.add(existingInterest);
            } else {
                throw new RuntimeException("Interest with label '" + interest.getLabel() + "' does not exist in the database.");
            }
        }
        user.setInterests(existingInterests);
        userRepository.save(user);
    }

    @Transactional
    public User update(int id, User user) {
        User userToChange = userRepository.findById(id).orElseThrow(() -> new ResourceAccessException("Not found user"));
        Set<String> labels = user.getInterests().stream().map(Interest::getLabel).collect(Collectors.toSet());
        List<Interest> interests = new ArrayList<>();
        for (String label : labels) {
            Interest interestFromDb = interestRepository.findInterestByLabel(label);
            if (interestFromDb != null) {
                interests.add(interestFromDb);
            } else {
                return null;
            }
        }
        userToChange.setUserName(user.getUserName());
        userToChange.setEmail(user.getEmail());
        userToChange.setFullName(user.getFullName());
        userToChange.setInterests(interests);
        userRepository.save(userToChange);
        return userToChange;
    }

    @Transactional
    public void delete(int id) {
        userRepository.deleteById(id);
    }
}

package ru.test.task.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.ResourceAccessException;
import ru.test.task.models.Interest;
import ru.test.task.models.User;
import ru.test.task.repositories.InterestRepository;
import ru.test.task.repositories.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class InterestService {
    private final UserRepository userRepository;
    private final InterestRepository interestRepository;

    @Autowired
    public InterestService(UserRepository userRepository, InterestRepository interestRepository) {
        this.userRepository = userRepository;
        this.interestRepository = interestRepository;
    }

    public Interest findById(int id) {
        return interestRepository.findById(id).orElse(null);
    }

    public List<User> findAllUserById(int id) {
        return userRepository.findUserByInterestsId(id);
    }

    @Transactional
    public Interest updateInterest(int id, Interest toUpdate) {
        var oldInterest = interestRepository.findById(id).orElse(null);
        if (oldInterest != null) {
            oldInterest.setUsers(toUpdate.getUsers());
            oldInterest.setLabel(toUpdate.getLabel().toLowerCase());
            interestRepository.save(oldInterest);
        }
        return oldInterest;
    }

    @Transactional
    public List<Interest> addInterestForUser(int idUser, Interest interest) {
        Optional<User> userOptional = userRepository.findById(idUser);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            Interest existingInterest = interestRepository.findInterestByLabel(interest.getLabel().toLowerCase());
            if (!user.getInterests().contains(existingInterest)) {
                if (existingInterest != null) {
                    user.getInterests().add(existingInterest);
                    userRepository.save(user);
                    return new ArrayList<>(user.getInterests());
                }
            }
        }
        return null;
    }

    @Transactional
    public boolean deleteInterestForUser(int userId, int interestId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceAccessException("Not found user with id = " + userId));
        Optional<Interest> interesting = user.getInterests()
                .stream().filter(interest -> interest.getId() == interestId).findFirst();
        if (interesting.isEmpty()) {
            return false;
        }
        user.removeInterest(interestId);
        return true;
    }

    @Transactional
    public Interest createNewInterest(Interest interest) {
        if (interest == null || interest.getLabel() == null || interest.getLabel().isEmpty()) {
            return null;
        }
        List<Interest> interests = findAllInterests();
        long count = interests.stream().filter((interestBD) ->
                interestBD.getLabel().equalsIgnoreCase(interest.getLabel())).count();
        if (count != 0) {
            return null;
        }
        interest.setLabel(interest.getLabel().toLowerCase());
        interestRepository.save(interest);
        return interest;
    }

    @Transactional
    public void deleteInterest(int interestId) {
        interestRepository.deleteById(interestId);
    }

    public List<Interest> findAllInterests() {
        return interestRepository.findAll();
    }

    public List<Interest> findByUserId(int id) {
        return interestRepository.findInterestsByUsersId(id);
    }

    public Optional<Interest> findByLabel(String label) {
        return Optional.ofNullable(interestRepository.findInterestByLabel(label));
    }
}

package ru.test.task.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.test.task.models.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    List<User> findUserByInterestsId(Integer interestId);

    Optional<User> findUserByEmail(String email);

    Optional<User> findUserByFullName(String fullName);

    List<User> findByUserNameContaining(String username);

}

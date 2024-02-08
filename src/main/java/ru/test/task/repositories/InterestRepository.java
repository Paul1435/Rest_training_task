package ru.test.task.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.test.task.models.Interest;

import java.util.List;

public interface InterestRepository extends JpaRepository<Interest, Integer> {
    List<Interest> findInterestsByUsersId(Integer userId);



    Interest findInterestByLabel(String label);
}

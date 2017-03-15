package com.onkibot.backend.database.repositories;

import com.onkibot.backend.database.entities.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Integer> {
    Optional<User> findByUserId(int userId);

    Optional<User> findByEmail(String email);
}

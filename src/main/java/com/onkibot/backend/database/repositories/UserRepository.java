package com.onkibot.backend.database.repositories;

import com.onkibot.backend.database.entities.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Integer> {
    User findByEmail(String email);
}

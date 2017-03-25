package com.onkibot.backend.database.repositories;

import com.onkibot.backend.database.entities.User;
import org.springframework.context.annotation.Bean;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {
    Optional<User> findByUserId(int userId);

    Optional<User> findByEmail(String email);
}

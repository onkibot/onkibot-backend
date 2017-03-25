package com.onkibot.backend.database.repositories;

import com.onkibot.backend.database.entities.User;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {
  Optional<User> findByUserId(int userId);

  Optional<User> findByEmail(String email);
}

package com.poweroftwo.potms_backend.user.repository;

import com.poweroftwo.potms_backend.user.repository.entities.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Integer> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}

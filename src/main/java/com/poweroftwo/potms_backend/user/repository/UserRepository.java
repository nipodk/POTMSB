package com.poweroftwo.potms_backend.user.repository;

import com.poweroftwo.potms_backend.user.repository.entities.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Integer> {
}

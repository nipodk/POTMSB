package com.poweroftwo.potms_backend.access_key.repository;

import com.poweroftwo.potms_backend.access_key.entity.Key;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AccessKeyRepository extends JpaRepository<Key, Integer> {
    @Query("SELECT k from Key k where k.userId = :userId")
    List<Key> findAllByUser(@Param("userId") Integer userId);

    @Query("SELECT count(k) from Key k where k.keyName = :keyName")
    Integer countByName(@Param("keyName") String name);

    @Query("SELECT k from Key k where k.keyName = :keyName and k.userId = :userId")
    Optional<Key> findUserKey (String keyName, Integer userId);
}

package com.sapient.login.repository;

import com.sapient.login.repository.entity.UserProfileEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserProfileRepository extends CrudRepository<UserProfileEntity, Integer> {

    Optional<UserProfileEntity> findByEmailId(String emailId);
}

package com.sapient.login.repository;

import com.sapient.login.repository.entity.UserProfileEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserProfileRepository extends MongoRepository<UserProfileEntity, String> {

    Optional<UserProfileEntity> findByEmailId(String emailId);
}

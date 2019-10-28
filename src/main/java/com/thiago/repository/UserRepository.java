package com.thiago.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.thiago.model.User;

public interface UserRepository extends MongoRepository<User, String> {

	@Query(value = "{ age : ?0 }")
	public List<User> findUsersByAge(int age);
}

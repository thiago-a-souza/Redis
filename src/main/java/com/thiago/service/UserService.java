package com.thiago.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.thiago.model.User;
import com.thiago.repository.UserRepository;

@Service
public class UserService {
	@Autowired
	private UserRepository repository;
	
	@Cacheable(cacheNames="userCache", key="#email", unless = "#result == null")
	public User findUserByEmail(String email) {
	  return repository.findById(email).orElse(null);
	}
	
	@Cacheable(cacheNames="usersAgeCache", key="#age", unless = "#result.size() > 10")
	public List<User> findUsersByAge(int age){
		return repository.findUsersByAge(age);
	}
	
	@Cacheable(value="userCache", key="#user.email", unless = "#result == null")
	public User addUser(User user) {
		return repository.insert(user);
	}
	
	@CachePut(value="userCache", key="#user.email", unless = "#result == null")
	public User updateUser(User user) {
		return repository.save(user);
	}
	
	@CacheEvict(value="userCache", key="#email")
	public void deleteUser(String email) {
		repository.deleteById(email);
	}
	
	@CacheEvict(value="userCache", allEntries=true)
	public void clearUserCache() {
		
	}
}

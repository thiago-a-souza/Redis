package com.thiago.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thiago.model.User;
import com.thiago.service.UserService;

@RestController
@RequestMapping(value="/user")
public class UserController {
	@Autowired
	private UserService userService;
	
	@PostMapping
	public User add(@RequestBody User user) {
		return userService.addUser(user);
	}

	@GetMapping("/{email}")
	public User getByEmail(@PathVariable("email") String email) {
		return userService.findUserByEmail(email);
	}

	
	@PutMapping
	public User update(@RequestBody User user) {
		return userService.updateUser(user);
	}

	
	@GetMapping("/findByAge/{age}")
	public List<User> getByAge(@PathVariable("age") int age) {
		return userService.findUsersByAge(age);
	}
	
	@DeleteMapping("/deleteUser/{email}")
	public void delete(@PathVariable("email") String email) {
		userService.deleteUser(email);
	}

	@DeleteMapping("/clearUserCache")
	public void clearUserCache() {
		userService.clearUserCache();
	}

}

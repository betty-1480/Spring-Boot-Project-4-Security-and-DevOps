package com.example.demo.controllers;


import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;

@RestController
@RequestMapping("/api/user")
public class UserController {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private CartRepository cartRepository;

	private final Logger log= LogManager.getLogger(UserController.class);

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@GetMapping("/id/{id}")
	public ResponseEntity<User> findById(@PathVariable Long id) {
		return ResponseEntity.of(userRepository.findById(id));
	}
	
	@GetMapping("/{username}")
	public ResponseEntity<User> findByUserName(@PathVariable String username) {
		User user = userRepository.findByUsername(username);
		return user == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(user);
	}
	
	@PostMapping("/create")
	public ResponseEntity<User> createUser(@RequestBody CreateUserRequest createUserRequest) {
		User user = new User();
		user.setUsername(createUserRequest.getUsername());

	//Password: requirements and validations
		if(createUserRequest.getPassword().length()<7)	{
	//SLF4J API - Logging facade for Java, decouple application from underlying Logger
			log.warn("Create user account failed - Password length less than seven for user {}",createUserRequest.getUsername());
			log.error("Create user account failed for user {} ",createUserRequest.getUsername());
			return ResponseEntity.badRequest().build();
		}

		if(!createUserRequest.getPassword().equals(createUserRequest.getConfirmPassword())){
			log.warn("Create user account failed - Password does not match with confirm password for user {}",createUserRequest.getUsername());
			log.error("Create user account failed for user {} ",createUserRequest.getUsername());
			return ResponseEntity.badRequest().build();
		}

	//Password is not encrypted but hashed
	//Authentication Scheme: Hashing, using BCrypt algorithm ((hashing+salting))
	//SpringBoot hashing tool: security.BCryptPasswordEncoder
		user.setPassword(bCryptPasswordEncoder.encode(createUserRequest.getPassword()));

		Cart cart = new Cart();
		cartRepository.save(cart);
		user.setCart(cart);
		userRepository.save(user);
		log.info("A user named {} is created successfully!",createUserRequest.getUsername());
		return ResponseEntity.ok(user);
	}

}

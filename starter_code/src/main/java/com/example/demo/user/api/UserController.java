package com.example.demo.user.api;

import com.example.demo.cart.service.CartService;
import com.example.demo.user.model.requests.CreateUserRequest;
import com.example.demo.user.service.UserService;
import com.example.demo.user.model.persistence.User;
import org.fissore.slf4j.FluentLogger;
import org.fissore.slf4j.FluentLoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.cart.model.persistence.Cart;

@RestController
@RequestMapping("/api/user")
public class UserController {

	private static final FluentLogger LOGGER = FluentLoggerFactory.getLogger(UserController.class);
	private final UserService userService;
	private final CartService cartService;

	public UserController(UserService userService, CartService cartService) {
		this.userService = userService;
		this.cartService = cartService;
	}

	@GetMapping("/id/{id}")
	public ResponseEntity<User> findById(@PathVariable Long id) {
		return ResponseEntity.of(userService.findById(id));
	}
	
	@GetMapping("/{username}")
	public ResponseEntity<User> findByUserName(@PathVariable String username) {
		User user = userService.findByUsername(username);
		return user == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(user);
	}
	
	@PostMapping("/create")
	public ResponseEntity<User> createUser(@RequestBody CreateUserRequest createUserRequest) {
		if (createUserRequest.getPassword().length() < 7
				|| !createUserRequest.getPassword().equals(createUserRequest.getConfirmPassword())) {
			LOGGER.error().log("Error with user password. Cannot create user {}", createUserRequest.getUsername());
			return ResponseEntity.badRequest().build();
		}

		User user = userService.createUser(createUserRequest);
		if (user == null || user.getId() < 0) {
			LOGGER.error().log("Error creating user {}", createUserRequest.getUsername());
			return ResponseEntity.badRequest().build();
		}

		Cart cart = cartService.createCart();
		if (cart == null || cart.getId() < 0) {
			LOGGER.error().log("Error creating cart. Cannot create cart for user {}", createUserRequest.getUsername());
			return ResponseEntity.badRequest().build();
		}

		boolean wasCartSaved = cartService.saveCart(cart);
		if (wasCartSaved) {
			userService.setCart(user, cart);
			userService.setPassword(user, createUserRequest.getPassword());

			boolean wasUserSaved = userService.saveUser(user);
			if (wasUserSaved) {
				LOGGER.info().log("User {} was created successfully", createUserRequest.getUsername());
				return ResponseEntity.ok(user);
			}
		}

		LOGGER.error().log("User {} could not be saved", createUserRequest.getUsername());
		return ResponseEntity.badRequest().build();
	}
	
}

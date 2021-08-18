package com.example.demo.cart.api;

import java.util.Optional;
import java.util.stream.IntStream;

import com.example.demo.cart.CartService;
import com.example.demo.cart.model.persistence.Cart;
import com.example.demo.cart.model.requests.ModifyCartRequest;
import com.example.demo.item.ItemService;
import com.example.demo.user.service.UserService;
import org.fissore.slf4j.FluentLogger;
import org.fissore.slf4j.FluentLoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.item.model.persistence.Item;
import com.example.demo.user.model.persistence.User;

@RestController
@RequestMapping("/api/cart")
public class CartController {

	private final FluentLogger LOGGER = FluentLoggerFactory.getLogger(CartController.class);
	private final UserService userService;
	private final CartService cartService;
	private final ItemService itemService;

	public CartController(UserService userService, CartService cartService, ItemService itemService) {
		this.userService = userService;
		this.cartService = cartService;
		this.itemService = itemService;
	}
	
	@PostMapping("/addToCart")
	public ResponseEntity<Cart> addToCart(@RequestBody ModifyCartRequest request) {
		User user = userService.findByUsername(request.getUsername());

		if(user == null) {
			LOGGER.error().log("User {} was not found. Cannot add to {}'s cart", request.getUsername());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}

		Optional<Item> item = itemService.findById(request.getItemId());
		if(!item.isPresent()) {
			LOGGER.error().log("Error finding Item id {}. Cannot add to {}'s cart", request.getItemId(), request.getUsername());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}

		Cart cart = user.getCart();
		IntStream.range(0, request.getQuantity())
			.forEach(i -> cart.addItem(item.get()));

		boolean result = cartService.saveCart(cart);
		if (result) {
			LOGGER.info().log("Successfully added {} to {}'s cart", request.getItemId(), request.getUsername());
			return ResponseEntity.ok(cart);
		}

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
	}
	
	@PostMapping("/removeFromCart")
	public ResponseEntity<Cart> removeFromCart(@RequestBody ModifyCartRequest request) {
		User user = userService.findByUsername(request.getUsername());

		if(user == null) {
			LOGGER.error().log("User {} was not found. Cannot remove from {}'s cart", request.getUsername());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}

		Optional<Item> item = itemService.findById(request.getItemId());
		if(!item.isPresent()) {
			LOGGER.error().log("Error finding Item id {}. Cannot remove from {}'s cart", request.getItemId(), request.getUsername());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}

		Cart cart = user.getCart();
		IntStream.range(0, request.getQuantity())
			.forEach(i -> cart.removeItem(item.get()));

		boolean result = cartService.saveCart(cart);
		if (result) {
			LOGGER.info().log("Successfully removed {} to {}'s cart", request.getItemId(), request.getUsername());
			return ResponseEntity.ok(cart);
		}

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
	}
}

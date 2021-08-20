package com.example.demo.order.api;

import java.util.List;

import com.example.demo.order.service.OrderService;
import com.example.demo.user.service.UserService;
import org.fissore.slf4j.FluentLogger;
import org.fissore.slf4j.FluentLoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.user.model.persistence.User;
import com.example.demo.order.model.requests.UserOrder;

@RestController
@RequestMapping("/api/order")
public class OrderController {

	private static final FluentLogger LOGGER = FluentLoggerFactory.getLogger(OrderController.class);
	private final UserService userService;
	private final OrderService orderService;

	public OrderController(UserService userService, OrderService orderService) {
		this.userService = userService;
		this.orderService = orderService;
	}
	
	
	@PostMapping("/submit/{username}")
	public ResponseEntity<UserOrder> submit(@PathVariable String username) {
		User user = userService.findByUsername(username);
		if(user == null) {
			LOGGER.error().log("Error finding user {}", username);
			return ResponseEntity.notFound().build();
		}

		UserOrder order = UserOrder.createFromCart(user.getCart());
		boolean result = orderService.saveOrder(order);
		if (result) {
			LOGGER.info().log("Order was submitted for {}", username);
			return ResponseEntity.ok(order);
		}

		LOGGER.warn().log("Order could not be submitted for {}", username);
		return ResponseEntity.unprocessableEntity().build();
	}
	
	@GetMapping("/history/{username}")
	public ResponseEntity<List<UserOrder>> getOrdersForUser(@PathVariable String username) {
		User user = userService.findByUsername(username);
		if(user == null) {
			LOGGER.error().log("Error finding user {}", username);
			return ResponseEntity.notFound().build();
		}

		List<UserOrder> userOrders = orderService.findByUser(user);
		if (userOrders.isEmpty()) {
			LOGGER.info().log("User {} does not have any orders", user.getUsername());
		}

		LOGGER.info().log("Returning a list of orders for user {}", user.getUsername());
		return ResponseEntity.ok(userOrders);
	}
}

package com.example.demo.controllers;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@RestController
@RequestMapping("/api/order")
public class OrderController {

	private static final Logger log = LogManager.getLogger(OrderController.class);

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private OrderRepository orderRepository;

	@PostMapping("/submit/{username}")
	public ResponseEntity<UserOrder> submit(@PathVariable String username) {
		log.info("===Start submit orders for user {}===", username);

		User user = userRepository.findByUsername(username);
		if(user == null) {
			log.error("Failed to find user {}", username);
			log.info("===End submit orders for user===");
			return ResponseEntity.notFound().build();
		}
		UserOrder order = UserOrder.createFromCart(user.getCart());
		orderRepository.save(order);
		log.info("Successfully created order of user{}", username);

		log.info("===End submit orders for user===");
		return ResponseEntity.ok(order);
	}
	
	@GetMapping("/history/{username}")
	public ResponseEntity<List<UserOrder>> getOrdersForUser(@PathVariable String username) {
		log.info("===Start get orders for user {}===", username);

		User user = userRepository.findByUsername(username);
		if(user == null) {
			log.error("Failed to find user {}", username);
			log.info("===End get orders for user===");
			return ResponseEntity.notFound().build();
		}
		log.info("Successfully found order for user {}", username);

		log.info("===End get orders for user===");
		return ResponseEntity.ok(orderRepository. findByUser(user));
	}
}

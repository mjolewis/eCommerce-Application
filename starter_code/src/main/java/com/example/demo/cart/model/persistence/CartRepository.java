package com.example.demo.cart.model.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.user.model.persistence.User;

public interface CartRepository extends JpaRepository<Cart, Long> {
	Cart findByUser(User user);
}

package com.example.demo.order.model.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.user.model.persistence.User;
import com.example.demo.user.model.requests.UserOrder;

public interface OrderRepository extends JpaRepository<UserOrder, Long> {
	List<UserOrder> findByUser(User user);
}

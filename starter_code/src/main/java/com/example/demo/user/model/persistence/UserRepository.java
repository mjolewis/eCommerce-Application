package com.example.demo.user.model.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.user.model.persistence.User;

public interface UserRepository extends JpaRepository<User, Long> {
	User findByUsername(String username);
}

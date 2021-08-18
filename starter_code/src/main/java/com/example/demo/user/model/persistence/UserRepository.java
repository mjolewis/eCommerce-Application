package com.example.demo.user.model.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.user.model.persistence.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	User findByUsername(String username);
}

package com.example.demo.user.service;

import com.example.demo.cart.model.persistence.Cart;
import com.example.demo.user.model.persistence.User;
import com.example.demo.user.model.persistence.UserRepository;
import com.example.demo.user.model.requests.CreateUserRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.encoder = encoder;
    }

    public User createUser(CreateUserRequest createUserRequest) {
        User user = new User();
        user.setUsername(createUserRequest.getUsername());
        return user;
    }

    public void setCart(User user, Cart cart) {
        user.setCart(cart);
    }

    public void setPassword(User user, String password) {
        user.setPassword(encoder.encode(password));
    }

    public boolean saveUser(User user) {
        User savedUser = userRepository.save(user);
        return savedUser.getId() > 0;
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}

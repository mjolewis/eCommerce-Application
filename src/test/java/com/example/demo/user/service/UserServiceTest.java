package com.example.demo.user.service;

import static org.mockito.Mockito.*;

import com.example.demo.cart.model.persistence.Cart;
import com.example.demo.user.model.persistence.User;
import com.example.demo.user.model.persistence.UserRepository;
import com.example.demo.user.model.requests.CreateUserRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    private CreateUserRequest createUserRequest;

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder encoder;

    @BeforeEach
    public void setup() {
        createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("username");
        createUserRequest.setPassword("password");
        createUserRequest.setConfirmPassword("password");
    }

    @Test
    public void createUserReturnsNewUser() {
        User user = userService.createUser(createUserRequest);

        Assertions.assertAll(
                () -> Assertions.assertNotNull(user),
                () -> Assertions.assertEquals(createUserRequest.getUsername(), user.getUsername())
        );
    }

    @Test
    public void setPasswordForUserConvertsPlainTextPasswordToSecurePasswordBeforeSaving() {
        when(encoder.encode(createUserRequest.getPassword())).thenReturn("hashedPassword");

        User user = userService.createUser(createUserRequest);
        userService.setPassword(user, createUserRequest.getPassword());

        Assertions.assertAll(
                () -> Assertions.assertNotNull(user),
                () -> Assertions.assertEquals("hashedPassword", user.getPassword())
        );
    }

    @Test
    public void saveUserToDatabase() {
        User user = userService.createUser(createUserRequest);

        when(userRepository.save(user)).thenReturn(user);

        boolean result = userService.saveUser(user);

        Assertions.assertAll(
                () -> Assertions.assertNotNull(user),
                () -> Assertions.assertTrue(result)
        );
    }

    @Test
    public void findUserByIdReturnsUser() {
        User user = userService.createUser(createUserRequest);
        when(userRepository.save(user)).thenReturn(user);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        userService.saveUser(user);
        Optional<User> optionalUser = userService.findById(user.getId());

        Assertions.assertAll(
                () -> Assertions.assertTrue(optionalUser.isPresent()),
                () -> Assertions.assertEquals(user.getId(), optionalUser.get().getId()),
                () -> Assertions.assertEquals(user.getUsername(), optionalUser.get().getUsername()),
                () -> Assertions.assertEquals(user.getPassword(), optionalUser.get().getPassword())
        );
    }

    @Test
    public void findUserByNameReturnsUser() {
        User user = userService.createUser(createUserRequest);

        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);

        User savedUser = userService.findByUsername(user.getUsername());

        Assertions.assertAll(
                () -> Assertions.assertNotNull(savedUser),
                () -> Assertions.assertEquals(user.getUsername(), savedUser.getUsername()),
                () -> Assertions.assertEquals(user.getPassword(), savedUser.getPassword())
        );
    }

    @Test
    public void setCartForUser() {
        User user = userService.createUser(createUserRequest);
        Cart cart = new Cart();
        cart.setUser(user);

        userService.setCart(user, cart);

        Assertions.assertAll(
                () -> Assertions.assertEquals(cart.getUser(), user),
                () -> Assertions.assertEquals(user.getCart(), cart)
        );
    }
}

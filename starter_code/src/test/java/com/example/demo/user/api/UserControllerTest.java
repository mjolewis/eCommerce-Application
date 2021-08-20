package com.example.demo.user.api;

import static org.mockito.Mockito.*;

import com.example.demo.cart.CartService;
import com.example.demo.cart.model.persistence.Cart;
import com.example.demo.user.model.persistence.User;
import com.example.demo.user.model.requests.CreateUserRequest;
import com.example.demo.user.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    private CreateUserRequest createUserRequest;
    private User user;
    private Cart cart;

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    @Mock
    private CartService cartService;

    @Mock
    private BCryptPasswordEncoder encoder;

    @BeforeEach
    public void setup() {
        createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("username");
        createUserRequest.setPassword("password");
        createUserRequest.setConfirmPassword("password");

        cart = new Cart();
        cart.setId(0L);

        user = new User();
        user.setId(0);
        user.setUsername("username");
        user.setPassword("hashedPassword");
    }

    @Test
    public void createNewUserReturns200Status() {
        when(userService.createUser(createUserRequest)).thenReturn(user);
        when(cartService.createCart()).thenReturn(cart);
        when(cartService.saveCart(cart)).thenReturn(true);
        when(userService.saveUser(user)).thenReturn(true);

        final ResponseEntity<User> response = userController.createUser(createUserRequest);

        User user = response.getBody();

        Assertions.assertAll(
                () -> Assertions.assertNotNull(response),
                () -> Assertions.assertEquals(200, response.getStatusCodeValue()),
                () -> Assertions.assertNotNull(user),
                () -> Assertions.assertEquals(0, user.getId()),
                () -> Assertions.assertEquals("username", user.getUsername()),
                () -> Assertions.assertEquals("hashedPassword", user.getPassword())
        );
    }

    @Test
    public void createUserWithInvalidPasswordReturnsBadRequestResponse() {
        CreateUserRequest createUserRequest = new CreateUserRequest();

        createUserRequest.setUsername("username");
        createUserRequest.setPassword("123");
        createUserRequest.setConfirmPassword("123");

        final ResponseEntity<User> response = userController.createUser(createUserRequest);

        Assertions.assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    public void createUserFailureReturnsBadRequestResponse() {
        when(userService.createUser(createUserRequest)).thenReturn(null);

        final ResponseEntity<User> response = userController.createUser(createUserRequest);

        Assertions.assertAll(
                () -> Assertions.assertNotNull(response),
                () -> Assertions.assertEquals(400, response.getStatusCodeValue())
        );
    }

    @Test
    public void createNewUserWithInvalidCartReturnsBadRequestResponse() {
        when(userService.createUser(createUserRequest)).thenReturn(user);
        when(cartService.createCart()).thenReturn(null);

        final ResponseEntity<User> response = userController.createUser(createUserRequest);

        Assertions.assertAll(
                () -> Assertions.assertNotNull(response),
                () -> Assertions.assertEquals(400, response.getStatusCodeValue())
        );
    }

    @Test
    public void findUserByNameReturnsMatchingUser() {
        when(userService.findByUsername(createUserRequest.getUsername())).thenReturn(user);

        final ResponseEntity<User> response = userController.findByUserName(createUserRequest.getUsername());

        Assertions.assertAll(
                () -> Assertions.assertNotNull(response),
                () -> Assertions.assertEquals("username", response.getBody().getUsername())
        );
    }
}

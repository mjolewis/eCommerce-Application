package com.example.demo.cart.api;

import static org.mockito.Mockito.*;

import com.example.demo.cart.service.CartService;
import com.example.demo.cart.model.persistence.Cart;
import com.example.demo.cart.model.requests.ModifyCartRequest;
import com.example.demo.item.service.ItemService;
import com.example.demo.item.model.persistence.Item;
import com.example.demo.user.model.persistence.User;
import com.example.demo.user.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class CartControllerTest {

    @InjectMocks
    private CartController cartController;

    @Mock
    private UserService userService;

    @Mock
    private CartService cartService;

    @Mock
    private ItemService itemService;

    private User user;
    private Cart cart;
    private Item itemOne;
    private Item itemTwo;

    @BeforeEach
    public void setup() {
        user = new User();
        user.setId(1L);
        user.setUsername("user");
        user.setPassword("password");

        itemOne = new Item();
        itemOne.setId(1L);
        itemOne.setDescription("Analysis of Algorithms");
        itemOne.setPrice(new BigDecimal("150.00"));

        itemTwo = new Item();
        itemTwo.setId(2L);
        itemTwo.setDescription("Financial Technology");
        itemTwo.setPrice(new BigDecimal("90.83"));

        cart = new Cart();
        cart.setId(1L);

        user.setCart(cart);
        cart.setUser(user);
    }

    @Test
    public void addToCartWhenUserIsNullReturnsAn404StatusCode() {
        ModifyCartRequest invalidCartRequest = new ModifyCartRequest();
        invalidCartRequest.setUsername("unknown user");

        when(userService.findByUsername(invalidCartRequest.getUsername())).thenReturn(null);

        ResponseEntity<Cart> response = cartController.addToCart(invalidCartRequest);

        Assertions.assertAll(
                () -> Assertions.assertNotNull(response),
                () -> Assertions.assertEquals(404, response.getStatusCodeValue())
        );
    }

    @Test
    public void addToCartWhenUserExistsButItemDoesNotReturnsAn404StatusCode() {
        ModifyCartRequest modifyCartRequestWithInvalidItem = new ModifyCartRequest();
        modifyCartRequestWithInvalidItem.setUsername(user.getUsername());
        modifyCartRequestWithInvalidItem.setItemId(3L);
        modifyCartRequestWithInvalidItem.setQuantity(5);

        when(userService.findByUsername(modifyCartRequestWithInvalidItem.getUsername())).thenReturn(user);
        when(itemService.findById(modifyCartRequestWithInvalidItem.getItemId())).thenReturn(Optional.empty());

        ResponseEntity<Cart> response = cartController.addToCart(modifyCartRequestWithInvalidItem);

        Assertions.assertAll(
                () -> Assertions.assertNotNull(response),
                () -> Assertions.assertEquals(404, response.getStatusCodeValue())
        );
    }

    @Test
    public void addToCartWhenUserExistsAndItemExistsShouldReturn200StatusCode() {
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setUsername(user.getUsername());
        modifyCartRequest.setItemId(1L);
        modifyCartRequest.setQuantity(10);

        when(userService.findByUsername(modifyCartRequest.getUsername())).thenReturn(user);
        when(itemService.findById(modifyCartRequest.getItemId())).thenReturn(Optional.ofNullable(itemOne));
        when(cartService.saveCart(user.getCart())).thenReturn(true);

        ResponseEntity<Cart> response = cartController.addToCart(modifyCartRequest);

        Assertions.assertAll(
                () -> Assertions.assertNotNull(response),
                () -> Assertions.assertEquals(200, response.getStatusCodeValue())
        );
    }

    @Test
    public void addToCartWhenUserExistsAndItemExistsButSavingCartFailedShouldReturn400StatusCode() {
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setUsername(user.getUsername());
        modifyCartRequest.setItemId(1L);
        modifyCartRequest.setQuantity(10);

        when(userService.findByUsername(modifyCartRequest.getUsername())).thenReturn(user);
        when(itemService.findById(modifyCartRequest.getItemId())).thenReturn(Optional.ofNullable(itemOne));
        when(cartService.saveCart(user.getCart())).thenReturn(false);

        ResponseEntity<Cart> response = cartController.addToCart(modifyCartRequest);

        Assertions.assertAll(
                () -> Assertions.assertNotNull(response),
                () -> Assertions.assertEquals(400, response.getStatusCodeValue())
        );
    }
}

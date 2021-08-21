package com.example.demo.order.api;

import static org.mockito.Mockito.*;

import com.example.demo.cart.model.persistence.Cart;
import com.example.demo.item.model.persistence.Item;
import com.example.demo.order.model.requests.UserOrder;
import com.example.demo.order.service.OrderService;
import com.example.demo.user.model.persistence.User;
import com.example.demo.user.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class OrderControllerTest {

    public User user;

    @InjectMocks
    private OrderController orderController;

    @Mock
    private UserService userService;

    @Mock
    private OrderService orderService;

    @BeforeEach
    public void setup() {
        user = new User();
        user.setUsername("user");
    }

    @Test
    public void userOrderSubmitFailsBecauseUserIsNotFound() {
        when(userService.findByUsername(user.getUsername())).thenReturn(null);

        ResponseEntity<UserOrder> response = orderController.submit(user.getUsername());

        Assertions.assertAll(
                () -> Assertions.assertNotNull(response),
                () -> Assertions.assertEquals(404, response.getStatusCodeValue())
        );
    }

    @Test
    public void userOrderIsSubmittedSuccessfully() {

        Item itemOne = new Item();
        itemOne.setId(1L);
        itemOne.setDescription("Analysis of Algorithms");
        itemOne.setPrice(new BigDecimal("150.00"));

        Item itemTwo = new Item();
        itemTwo.setId(2L);
        itemTwo.setDescription("Financial Technology");
        itemTwo.setPrice(new BigDecimal("90.83"));

        Cart cart = new Cart();
        cart.setId(1L);
        cart.setUser(user);
        cart.addItem(itemOne);
        cart.addItem(itemTwo);

        user.setCart(cart);

        UserOrder userOrder = new UserOrder();
        userOrder.setId(1L);
        userOrder.setUser(user);

        when(userService.findByUsername(user.getUsername())).thenReturn(user);
        when(orderService.saveOrder(Mockito.any(UserOrder.class))).thenReturn(true);

        ResponseEntity<UserOrder> response = orderController.submit(user.getUsername());

        Assertions.assertAll(
                () -> Assertions.assertNotNull(response),
                () -> Assertions.assertEquals(200, response.getStatusCodeValue()),
                () -> Assertions.assertEquals(cart.getTotal(), response.getBody().getTotal())
        );
    }

    @Test
    public void userOrderSubmitFailsBecauseOrderServiceCouldNotSaveOrder() {
        Item itemOne = new Item();
        itemOne.setId(1L);
        itemOne.setDescription("Analysis of Algorithms");
        itemOne.setPrice(new BigDecimal("150.00"));

        Item itemTwo = new Item();
        itemTwo.setId(2L);
        itemTwo.setDescription("Financial Technology");
        itemTwo.setPrice(new BigDecimal("90.83"));

        Cart cart = new Cart();
        cart.setId(1L);
        cart.setUser(user);
        cart.addItem(itemOne);
        cart.addItem(itemTwo);

        user.setCart(cart);

        UserOrder userOrder = new UserOrder();

        when(userService.findByUsername(user.getUsername())).thenReturn(user);
        when(orderService.saveOrder(Mockito.any(UserOrder.class))).thenReturn(false);

        ResponseEntity<UserOrder> response = orderController.submit(user.getUsername());

        Assertions.assertAll(
                () -> Assertions.assertNotNull(response),
                () -> Assertions.assertEquals(422, response.getStatusCodeValue())
        );
    }

    @Test
    public void getOrdersForUserThatDoesNotExistReturnsANotFoundResponse() {
        when(userService.findByUsername(user.getUsername())).thenReturn(null);

        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser(user.getUsername());

        Assertions.assertAll(
                () -> Assertions.assertNotNull(response),
                () -> Assertions.assertEquals(404, response.getStatusCodeValue())
        );
    }

    @Test
    public void getOrdersForUserWhoHasNotSubmittedAnyOrdersReturnsAnOkResponse() {
        List<UserOrder> userOrders = new ArrayList<>();
        when(userService.findByUsername(user.getUsername())).thenReturn(user);
        when(orderService.findByUser(user)).thenReturn(userOrders);

        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser(user.getUsername());

        Assertions.assertAll(
                () -> Assertions.assertNotNull(response),
                () -> Assertions.assertTrue(response.getBody().isEmpty()),
                () -> Assertions.assertEquals(200, response.getStatusCodeValue())
        );
    }

    @Test
    public void getOrdersForUserReturnsAllPreviouslySubmittedOrdersForThatUser() {
        UserOrder orderOne = new UserOrder();
        orderOne.setId(1L);
        orderOne.setUser(user);
        orderOne.setTotal(new BigDecimal("10.00"));

        UserOrder orderTwo = new UserOrder();
        orderTwo.setId(2L);
        orderTwo.setUser(user);
        orderTwo.setTotal(new BigDecimal("20.00"));

        List<UserOrder> userOrders = List.of(orderOne, orderTwo);

        when(userService.findByUsername(user.getUsername())).thenReturn(user);
        when(orderService.findByUser(user)).thenReturn(userOrders);

        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser(user.getUsername());

        Assertions.assertAll(
                () -> Assertions.assertNotNull(response),
                () -> Assertions.assertEquals(orderOne.getId(), response.getBody().get(0).getId()),
                () -> Assertions.assertEquals(orderTwo.getId(), response.getBody().get(1).getId()),
                () -> Assertions.assertEquals(200, response.getStatusCodeValue()),
                () -> Assertions.assertEquals(orderOne.getTotal(), response.getBody().get(0).getTotal()),
                () -> Assertions.assertEquals(orderTwo.getTotal(), response.getBody().get(1).getTotal())
        );
    }
}

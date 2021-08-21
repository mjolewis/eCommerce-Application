package com.example.demo.order.service;

import static org.mockito.Mockito.*;

import com.example.demo.cart.model.persistence.Cart;
import com.example.demo.item.model.persistence.Item;
import com.example.demo.order.model.persistence.OrderRepository;
import com.example.demo.order.model.requests.UserOrder;
import com.example.demo.user.model.persistence.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;

    private User user;
    private UserOrder userOrder;

    @BeforeEach
    public void setup() {
        user = new User();
        user.setId(1L);
        user.setUsername("user");
        user.setPassword("password");

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

        userOrder = new UserOrder();
        userOrder.setId(1L);
        userOrder.setUser(user);
    }

    @Test
    public void saveValidOrderReturnsTrue() {
        when(orderRepository.save(Mockito.any())).thenReturn(userOrder);

        boolean result = orderService.saveOrder(userOrder);

        Assertions.assertAll(
                () -> Assertions.assertNotNull(userOrder),
                () -> Assertions.assertTrue(result),
                () -> Assertions.assertTrue(userOrder.getId() >= 0)
        );
    }

    @Test
    public void findOrderByUserReturnsListOfTheUsersOrders() {
        when(orderRepository.findByUser(user)).thenReturn(List.of(userOrder));

        List<UserOrder> response = orderService.findByUser(user);

        Assertions.assertAll(
                () -> Assertions.assertNotNull(response),
                () -> Assertions.assertEquals(userOrder, response.get(0)),
                () -> Assertions.assertEquals(user, response.get(0).getUser())
        );
    }
}

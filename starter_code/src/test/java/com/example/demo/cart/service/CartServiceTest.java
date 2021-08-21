package com.example.demo.cart.service;

import static org.mockito.Mockito.when;

import com.example.demo.cart.model.persistence.Cart;
import com.example.demo.cart.model.persistence.CartRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CartServiceTest {

    @InjectMocks
    private CartService cartService;

    @Mock
    private CartRepository cartRepository;

    @Test
    public void createCartReturnsNewCart() {
        Cart cart = cartService.createCart();

        Assertions.assertNotNull(cart);
    }

    @Test
    public void saveCartWhenCartShouldReturnTrueWhenValidCartIsSaved() {
        Cart cart = cartService.createCart();
        cart.setId(1L);

        when(cartRepository.save(cart)).thenReturn(cart);

        boolean result = cartService.saveCart(cart);

        Assertions.assertTrue(result);
    }

    @Test
    public void saveCartWhenCartIsNullShouldReturnFalse() {
        Cart cart = cartService.createCart();
        cart.setId(-1L);

        when(cartRepository.save(Mockito.any())).thenReturn(cart);

        boolean result = cartService.saveCart(cart);

        Assertions.assertFalse(result);
    }
}

package com.example.demo.cart;

import com.example.demo.cart.model.persistence.Cart;
import com.example.demo.cart.model.persistence.CartRepository;
import org.springframework.stereotype.Service;

@Service
public class CartService {

    private final CartRepository cartRepository;

    public CartService(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    public Cart createCart() {
        return new Cart();
    }

    public boolean saveCart(Cart cart) {
        return cartRepository.save(cart).getId() > 0;
    }
}

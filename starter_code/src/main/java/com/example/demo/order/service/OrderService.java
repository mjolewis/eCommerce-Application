package com.example.demo.order.service;

import com.example.demo.order.model.persistence.OrderRepository;
import com.example.demo.order.model.requests.UserOrder;
import com.example.demo.user.model.persistence.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public boolean saveOrder(UserOrder userOrder) {
        return orderRepository.save(userOrder).getId()>=0;
    }

    public List<UserOrder> findByUser(User user) {
        return orderRepository.findByUser(user);
    }
}

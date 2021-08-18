package com.example.demo.item;

import com.example.demo.item.model.persistence.Item;
import com.example.demo.item.model.persistence.ItemRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ItemService {

    private final ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public Optional<Item> findById(Long id) {
        return itemRepository.findById(id);
    }
}

package com.example.demo.item.service;

import com.example.demo.item.model.persistence.Item;
import com.example.demo.item.model.persistence.ItemRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {

    @InjectMocks
    private ItemService itemService;

    @Mock
    private ItemRepository itemRepository;

    private List<Item> items;
    private Item itemOne;
    private Item itemTwo;

    @BeforeEach
    public void setup() {
        itemOne = new Item();
        itemOne.setId(1L);
        itemOne.setDescription("Book");
        itemOne.setPrice(new BigDecimal("150.00"));
        itemOne.setName("Analysis of Algorithms");

        itemTwo = new Item();
        itemTwo.setId(2L);
        itemTwo.setDescription("Book");
        itemTwo.setPrice(new BigDecimal("90.83"));
        itemTwo.setName("Financial Technology");

        items = new ArrayList<>();
        items.add(itemOne);
        items.add(itemTwo);
    }

    @Test
    public void findAllItems() {
        when(itemRepository.findAll()).thenReturn(items);

        List<Item> response = itemService.findAll();

        Assertions.assertAll(
                () -> Assertions.assertNotNull(response),
                () -> Assertions.assertEquals(itemOne, response.get(0)),
                () -> Assertions.assertEquals(itemTwo, response.get(1))
        );
    }

    @Test
    public void findItemById() {
        when(itemRepository.findById(1L)).thenReturn(Optional.ofNullable(itemOne));

        Optional<Item> response = itemService.findById(1L);

        Assertions.assertAll(
                () -> Assertions.assertNotNull(response),
                () -> Assertions.assertTrue(response.isPresent()),
                () -> Assertions.assertEquals(itemOne.getId(), response.get().getId()),
                () -> Assertions.assertEquals(itemOne.getPrice(), response.get().getPrice()),
                () -> Assertions.assertEquals(itemOne.getName(), response.get().getName()),
                () -> Assertions.assertEquals(itemOne.getDescription(), response.get().getDescription())
        );
    }

    @Test
    public void findItemsByName() {
        when(itemRepository.findByName(itemTwo.getName())).thenReturn(List.of(itemTwo));

        List<Item> response = itemService.findByName(itemTwo.getName());

        Assertions.assertAll(
                () -> Assertions.assertNotNull(response),
                () -> Assertions.assertEquals(itemTwo, response.get(0)),
                () -> Assertions.assertEquals(itemTwo.getId(), response.get(0).getId()),
                () -> Assertions.assertEquals(itemTwo.getPrice(), response.get(0).getPrice()),
                () -> Assertions.assertEquals(itemTwo.getName(), response.get(0).getName()),
                () -> Assertions.assertEquals(itemTwo.getDescription(), response.get(0).getDescription())
        );
    }

    @Test
    public void findItemsByNameReturnsEmptyListWhenItemIsNotFound() {
        when(itemService.findByName(Mockito.anyString())).thenReturn(List.of());

        List<Item> response = itemService.findByName(itemTwo.getName());

        Assertions.assertAll(
                () -> Assertions.assertNotNull(response),
                () -> Assertions.assertTrue(response.isEmpty())
        );
    }
}

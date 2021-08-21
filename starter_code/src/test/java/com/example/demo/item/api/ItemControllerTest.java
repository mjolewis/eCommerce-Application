package com.example.demo.item.api;

import static org.mockito.Mockito.*;

import com.example.demo.item.service.ItemService;
import com.example.demo.item.model.persistence.Item;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class ItemControllerTest {

    @InjectMocks
    private ItemController itemController;

    @Mock
    private ItemService itemService;

    private List<Item> items;
    private Item itemOne;
    private Item itemTwo;

    @BeforeEach
    public void setup() {
        itemOne = new Item();
        itemOne.setId(1L);
        itemOne.setDescription("Analysis of Algorithms");
        itemOne.setPrice(new BigDecimal("150.00"));

        itemTwo = new Item();
        itemTwo.setId(2L);
        itemTwo.setDescription("Financial Technology");
        itemTwo.setPrice(new BigDecimal("90.83"));

        items = new ArrayList<>();
        items.add(itemOne);
        items.add(itemTwo);
    }

    @Test
    public void getAllItems() {
        when(itemService.findAll()).thenReturn(items);

        ResponseEntity<List<Item>> response = itemController.getItems();

        Assertions.assertAll(
                () -> Assertions.assertNotNull(response),
                () -> Assertions.assertEquals(200, response.getStatusCodeValue())
        );
    }

    @Test
    public void getItemById() {
        when(itemService.findById(1L)).thenReturn(Optional.ofNullable(itemOne));

        ResponseEntity<Item> response = itemController.getItemById(1L);

        Assertions.assertAll(
                () -> Assertions.assertNotNull(response),
                () -> Assertions.assertEquals(itemOne.getId(), response.getBody().getId()),
                () -> Assertions.assertEquals(itemOne.getPrice(), response.getBody().getPrice()),
                () -> Assertions.assertEquals(itemOne.getName(), response.getBody().getName()),
                () -> Assertions.assertEquals(itemOne.getDescription(), response.getBody().getDescription())
        );
    }

    @Test
    public void getItemsByNameReturns200StatusWhenItemIsFound() {
        when(itemService.findByName(itemTwo.getName())).thenReturn(List.of(itemTwo));

        ResponseEntity<List<Item>> response = itemController.getItemsByName(itemTwo.getName());

        Assertions.assertAll(
                () -> Assertions.assertNotNull(response),
                () -> Assertions.assertEquals(200, response.getStatusCodeValue())
        );
    }

    @Test
    public void getItemsByNameReturns404StatusWhenItemIsNotFound() {
        when(itemService.findByName(itemTwo.getName())).thenReturn(null);

        ResponseEntity<List<Item>> response = itemController.getItemsByName(itemTwo.getName());

        Assertions.assertAll(
                () -> Assertions.assertNotNull(response),
                () -> Assertions.assertEquals(404, response.getStatusCodeValue())
        );
    }

    @Test
    public void getItemsByNameReturns404StatusWhenResultSetIsEmpty() {
        when(itemService.findByName(itemTwo.getName())).thenReturn(List.of());

        ResponseEntity<List<Item>> response = itemController.getItemsByName(itemTwo.getName());

        Assertions.assertAll(
                () -> Assertions.assertNotNull(response),
                () -> Assertions.assertEquals(404, response.getStatusCodeValue())
        );
    }
}

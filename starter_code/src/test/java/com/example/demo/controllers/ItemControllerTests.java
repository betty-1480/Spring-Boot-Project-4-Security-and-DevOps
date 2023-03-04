package com.example.demo.controllers;

import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
public class ItemControllerTests {

    @InjectMocks
    private ItemController itemController;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private Item item;

    @Before
    public void arrange(){
        given(itemRepository.findById(any())).willReturn(Optional.of(item));
        given(itemRepository.findByName(any())).willReturn(List.of(item));
    }

    @Test
    public void getItems_happy_path(){
        ResponseEntity<List<Item>> response= itemController.getItems();
        assertEquals(200,response.getStatusCodeValue());
        verify(itemRepository,times(1)).findAll();
    }

    @Test
    public void getItemById_happy_path(){
        ResponseEntity<Item> response=itemController.getItemById(1L);
        assertEquals(200,response.getStatusCodeValue());
        verify(itemRepository,times(1)).findById(1L);
    }

    @Test
    public void getItemsByName_happy_path(){
        ResponseEntity<List<Item>> response=itemController.getItemsByName("Betty");
        assertEquals(200,response.getStatusCodeValue());
        verify(itemRepository,times(1)).findByName("Betty");
    }

}

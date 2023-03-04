package com.example.demo.controllers;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.Assert.assertEquals;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
public class CartControllerTests {

    @InjectMocks
    private CartController cartController;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ItemRepository itemRepository;

    @Before
    public void arrange(){
        User user=new User();
        Item item=new Item();
        Cart cart=new Cart();
        user.setUsername("Betty");
        user.setCart(cart);
        item.setId(1L);
        item.setPrice(BigDecimal.valueOf(2.99));
        given(userRepository.findByUsername(any())).willReturn(user);
        given(userRepository.findByUsername(null)).willReturn(null);
        given(itemRepository.findById(any())).willReturn(Optional.of(item));
        given(itemRepository.findById(2L)).willReturn(Optional.empty());
        given(cartRepository.save(any())).willReturn(cart);

    }

    @Test
    public void addToCart_happy_path(){
        ModifyCartRequest modifyCartRequest=modifyCartRequest("Betty",1,1L);
        ResponseEntity<Cart> response=cartController.addTocart(modifyCartRequest);
        assertEquals(200,response.getStatusCodeValue());
        verify(userRepository,times(1)).findByUsername(modifyCartRequest.getUsername());
        verify(itemRepository,times(1)).findById(modifyCartRequest.getItemId());
        verify(cartRepository,times(1)).save(any());
    }

    @Test
    public void addToCart_with_null_user(){
        ModifyCartRequest modifyCartRequest=modifyCartRequest(null,2,2L);
        ResponseEntity<Cart> response=cartController.addTocart(modifyCartRequest);
        assertEquals(404,response.getStatusCodeValue());
    }

    @Test
    public void addToCart_with_item_not_present(){
        ModifyCartRequest modifyCartRequest=modifyCartRequest("Betty",2,2L);
        ResponseEntity<Cart> response=cartController.addTocart(modifyCartRequest);
        assertEquals(404,response.getStatusCodeValue());
    }

    @Test
    public void removeFromCart_happy_path(){
        ModifyCartRequest modifyCartRequest=modifyCartRequest("Betty",1,1L);
        ResponseEntity<Cart> response= cartController.removeFromcart(modifyCartRequest);
        assertEquals(200,response.getStatusCodeValue());
        verify(userRepository,times(1)).findByUsername(modifyCartRequest.getUsername());
        verify(itemRepository,times(1)).findById(modifyCartRequest.getItemId());
        verify(cartRepository,times(1)).save(any());
    }

    public void removeFromCart_with_null_user(){
        ModifyCartRequest modifyCartRequest=modifyCartRequest(null,2,2L);
        ResponseEntity<Cart> response=cartController.removeFromcart(modifyCartRequest);
        assertEquals(404,response.getStatusCodeValue());
    }

    @Test
    public void removeFromCart_with_item_not_present(){
        ModifyCartRequest modifyCartRequest=modifyCartRequest("Betty",2,2L);
        ResponseEntity<Cart> response=cartController.removeFromcart(modifyCartRequest);
        assertEquals(404,response.getStatusCodeValue());
    }

    private ModifyCartRequest modifyCartRequest(String username,int quantity,long itemId){
        ModifyCartRequest modifyCartRequest=new ModifyCartRequest();
        modifyCartRequest.setQuantity(quantity);
        modifyCartRequest.setUsername(username);
        modifyCartRequest.setItemId(itemId);
        return modifyCartRequest;
    }

}

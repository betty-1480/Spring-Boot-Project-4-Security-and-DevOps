package com.example.demo.controllers;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
public class OrderControllerTests {

    @InjectMocks
    OrderController orderController;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrderRepository orderRepository;

    @Before
    public void arrange(){
        User user=new User();
        user.setUsername("Betty");

        Cart cart=new Cart();

        UserOrder userOrder=new UserOrder();

        Item item1=new Item();
        item1.setId(2L);
        item1.setName("Square Widget");
        item1.setPrice(BigDecimal.valueOf(1.99));
        item1.setDescription("A widget that is square");

        Item item2=new Item();
        item2.setId(1L);
        item2.setName("Round Widget");
        item2.setPrice(BigDecimal.valueOf(2.99));
        item2.setDescription("A widget that is round");

        cart.setItems(List.of(item1,item2));
        user.setCart(cart);

        given(userRepository.findByUsername(any())).willReturn(user);
        given(userRepository.findByUsername(null)).willReturn(null);
    }

    @Test
    public void submit_happy_path(){
        ResponseEntity<UserOrder> response = orderController.submit("Betty");
        assertEquals(200,response.getStatusCodeValue());
        verify(userRepository,times(1)).findByUsername("Betty");
        verify(orderRepository,times(1)).save(any());
    }

    @Test
    public void submit_with_null_user(){
        ResponseEntity<UserOrder> response = orderController.submit(null);
        assertEquals(404,response.getStatusCodeValue());
    }

    @Test
    public void getOrdersForUser_get_happy_path(){
        ResponseEntity<List<UserOrder>> response=orderController.getOrdersForUser("Betty");
        verify(userRepository,times(1)).findByUsername(any());
        assertEquals(200,response.getStatusCodeValue());
    }

    public void getOrdersForUser_with_null_user(){
        ResponseEntity<List<UserOrder>> response=orderController.getOrdersForUser(null);
        verify(userRepository,times(1)).findByUsername(any());
        assertEquals(404,response.getStatusCodeValue());
    }

}

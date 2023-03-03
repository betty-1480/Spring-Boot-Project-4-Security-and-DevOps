package com.example.demo.controllers;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@RunWith(SpringRunner.class)
@WebMvcTest(UserController.class)
public class UserControllerTests {

    @Autowired
    private UserController userController;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private CartRepository cartRepository;

    @MockBean
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Mock
    Logger log;

    @Before
    public void arrange(){
        Cart cart=new Cart();
        User user=new User();
        given(cartRepository.save(any())).willReturn(cart);
        given(userRepository.save(any())).willReturn(user);
        given(bCryptPasswordEncoder.encode(any())).willReturn("Hashed password");
    }

    @Test
    public void create_user_happy_path(){
       CreateUserRequest createUserRequest= createUserRequest();
       ResponseEntity<User> response= userController.createUser(createUserRequest);
        User user= response.getBody();
        assert user != null;
        assertEquals (createUserRequest.getUsername(),user.getUsername());
    }

    private CreateUserRequest createUserRequest(){
        CreateUserRequest createUserRequest=new CreateUserRequest();
        createUserRequest.setUsername("Betty");
        createUserRequest.setPassword("password");
        createUserRequest.setConfirmPassword("password");
        return createUserRequest;
    }

}

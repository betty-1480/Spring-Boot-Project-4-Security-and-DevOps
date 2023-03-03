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
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
public class UserControllerTests {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
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
       CreateUserRequest createUserRequest= createUserRequest("Betty","password","password");
       ResponseEntity<User> response= userController.createUser(createUserRequest);
        User user= response.getBody();
        assert user != null;
        assertEquals (createUserRequest.getUsername(),user.getUsername());
        assertEquals(user.getPassword(),"Hashed password");
        assertEquals(200,response.getStatusCodeValue());
        verify(userRepository,times(1)).save(user);
    }

    @Test
    public void create_user_with_password_issues(){
        CreateUserRequest createUserRequest= createUserRequest("BettyS","password1","confirmPassword");
        ResponseEntity<User> response=userController.createUser(createUserRequest);
        User user=response.getBody();
        assertEquals(400,response.getStatusCodeValue());
        verify(log,times(1)).error("Error creating password for user {} ",createUserRequest.getUsername());
    }

    private CreateUserRequest createUserRequest(String username, String password, String confirmPassword){
        CreateUserRequest createUserRequest=new CreateUserRequest();
        createUserRequest.setUsername(username);
        createUserRequest.setPassword(password);
        createUserRequest.setConfirmPassword(confirmPassword);
        return createUserRequest;
    }

}

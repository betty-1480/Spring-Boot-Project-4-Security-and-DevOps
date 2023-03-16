package com.example.demo.security;

import com.auth0.jwt.JWT;
import com.example.demo.model.persistence.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;

//Class responsible for authentication process - Betty
public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter{

    //Ramesh - static class that manages the authentication modules of the application.
    private final AuthenticationManager authenticationManager;

    private final Logger logger= LogManager.getLogger(JWTAuthenticationFilter.class);

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager){
        this.authenticationManager=authenticationManager;
    }

    @Override
    //Performs authentication by filtering user credentials
    public Authentication attemptAuthentication(HttpServletRequest httpServletRequest,
                                                     HttpServletResponse httpServletResponse) throws AuthenticationException {
        try {
            User  user = new ObjectMapper().readValue(httpServletRequest.getInputStream(), User.class);
            assert user != null;
            UsernamePasswordAuthenticationToken token=new UsernamePasswordAuthenticationToken(user.getUsername(),user.getPassword(),new ArrayList<>());
            if(authenticationManager.authenticate(token).isAuthenticated()){
                logger.info("User {} logged in successfully",user.getUsername());
                return authenticationManager.authenticate(token);
            }
            else{
                logger.error("wrong username or pass");
                throw new Exception("wrong username or pass");
            }

        } catch (Exception e) {
            logger.error("Exception: wrong username or password");
             throw new RuntimeException(e); // To resolve the error: "missing return statement"
        }

    }

    @Override
    // This method will be called after successful authentication
    public void successfulAuthentication(HttpServletRequest httpServletRequest,
                                         HttpServletResponse httpServletResponse,
                                         FilterChain filterChain,
                                         Authentication authentication){

        //generate a JWT token for the user
        String token= JWT.create()
                .withSubject(((org.springframework.security.core.userdetails.User) authentication.getPrincipal()).getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME))
                .sign(HMAC512(SecurityConstants.SECRET.getBytes()));

        httpServletResponse.addHeader(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + token);
    }

}

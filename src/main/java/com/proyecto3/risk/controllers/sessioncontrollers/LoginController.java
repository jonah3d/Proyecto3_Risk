package com.proyecto3.risk.controllers.sessioncontrollers;

import com.proyecto3.risk.model.dtos.LoginRequestDto;
import com.proyecto3.risk.model.dtos.UserResponseDto;
import com.proyecto3.risk.model.entities.User;
import com.proyecto3.risk.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class LoginController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private ModelMapper modelMapper;


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto loginRequest) {
       try{
           Authentication auth = authenticationManager.authenticate(
                   new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
           );

           if (auth.isAuthenticated()) {

                // Here you can return the user details or any other information you want
               User user = userService.getUserByUserName(loginRequest.getUsername());

                if (user == null) {
                     return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
                }

               UserResponseDto responseDto = modelMapper.map(user, UserResponseDto.class);
               return new ResponseEntity<>(responseDto, HttpStatus.OK);
           } else {
               return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
           }
       }catch (AuthenticationException ex){
           return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
       }
    }

}
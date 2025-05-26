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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api")
public class LoginController {

    @Autowired
    private UserService userService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto loginRequest) {
        User user = userService.getUserByUserName(loginRequest.getUsername());

        System.out.println("ATTEMPTING TO LOGIN USER: " + loginRequest.getUsername() + " WITH PASSWORD: " + loginRequest.getPassword());
        System.out.println("DATA FROM USER: " + user);

        if (user == null || !passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }

        UserResponseDto responseDto = modelMapper.map(user, UserResponseDto.class);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }
}


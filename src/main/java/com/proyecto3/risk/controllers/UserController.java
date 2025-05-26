package com.proyecto3.risk.controllers;

import com.proyecto3.risk.configurations.IsSameUser;
import com.proyecto3.risk.model.dtos.UpdateUserDto;
import com.proyecto3.risk.model.dtos.UserRegistrationDto;
import com.proyecto3.risk.model.dtos.UserResponseDto;
import com.proyecto3.risk.model.entities.Avatars;
import com.proyecto3.risk.model.entities.User;
import com.proyecto3.risk.service.AvatarService;
import com.proyecto3.risk.service.UserService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@Validated
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AvatarService avatarService;

    @Autowired
    private ModelMapper modelMapper;

    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserRegistrationDto registrationDto) {
        try {
            // Get Avatar
            Avatars avatar = avatarService.GetAvatarById(registrationDto.getAvatarId());
            if (avatar == null) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No Avatar");
            }

            // Map and assign avatar
            User user = modelMapper.map(registrationDto, User.class);
            user.setAvatar(avatar);


            // Save
            userService.CreateUser(user);
            UserResponseDto responseDto = modelMapper.map(user, UserResponseDto.class);

            return new ResponseEntity<>(responseDto, HttpStatus.CREATED);

        } catch (IllegalArgumentException | IllegalStateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error registering user: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // This endpoint requires authentication and checks if the authenticated user
    // is the same as the one being updated yes
    @PutMapping("/update/{username}")
    @IsSameUser
    public ResponseEntity<?> updateUser(@PathVariable String username,
                                        @RequestBody UpdateUserDto updateUserDto,
                                        Authentication authentication) {
        try {
            User existingUser = userService.getUserByUserName(username);
            if (existingUser == null) {
                return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
            }

            Avatars avatar = avatarService.GetAvatarById(updateUserDto.getAvatarId());
            if (avatar == null) {
                return new ResponseEntity<>("Avatar not found with ID: " + updateUserDto.getAvatarId(),
                        HttpStatus.BAD_REQUEST);
            }


            // Map update DTO to User and preserve key fields
            User updatedUser = modelMapper.map(updateUserDto, User.class);
            updatedUser.setId(existingUser.getId());
            updatedUser.setAvatar(avatar);
            updatedUser.setUsername(existingUser.getUsername()); // Prevent username change

            try {
                userService.updateUser(existingUser.getId(), updatedUser);
            } catch (Exception e) {
                e.printStackTrace(); // This will show the full cause in the console
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            }


            // Return updated user DTO
            UserResponseDto responseDto = modelMapper.map(updatedUser, UserResponseDto.class);
            return new ResponseEntity<>(responseDto, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // This endpoint requires authentication and checks if the authenticated user
    // is the same as the one being deleted
    @DeleteMapping("/delete/{username}")
    @IsSameUser
    public ResponseEntity<?> deleteUser(@PathVariable String username,
                                        @RequestParam String password,
                                        Authentication authentication) {
        // Method level security already checks if it's the same user

        try {
            userService.deleteUser(username, password);
            return new ResponseEntity<>("User deleted successfully", HttpStatus.OK);
        } catch (SecurityException e) {
            return new ResponseEntity<>("Invalid credentials", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // Get all users - protected endpoint (for admin purposes)
    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        if (users == null || users.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        List<UserResponseDto> userResponseDtos = users.stream()
                .map(user -> modelMapper.map(user, UserResponseDto.class))
                .toList();
        return new ResponseEntity<>(userResponseDtos, HttpStatus.OK);
    }

    // Get user by username - can be used for profile viewing
    @GetMapping("/{username}")
    public ResponseEntity<UserResponseDto> getUserByUsername(@PathVariable String username) {
        User user = userService.getUserByUserName(username);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        UserResponseDto responseDto = modelMapper.map(user, UserResponseDto.class);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @GetMapping("/id/{userId}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long userId) {
        User user = userService.getUserById(userId);

        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        UserResponseDto responseDto = modelMapper.map(user, UserResponseDto.class);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @GetMapping("/{userId}/wins")
    public ResponseEntity<Integer> getUserWins(@PathVariable Long userId) {
        try {
            Integer wins = userService.getUserWins(userId);
            return new ResponseEntity<>(wins, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{userId}/games")
    public ResponseEntity<Integer> getUserGamesPlayed(@PathVariable Long userId) {
        try {
            Integer gamesPlayed = userService.getUserGamesPlayed(userId);
            return new ResponseEntity<>(gamesPlayed, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
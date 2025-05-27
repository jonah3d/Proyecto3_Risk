package com.proyecto3.risk.service;

import com.proyecto3.risk.exceptions.UserException;
import com.proyecto3.risk.model.entities.User;
import com.proyecto3.risk.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class UseServiceImp implements UserService{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UseServiceImp() {
    }

    @Override
    public List<User> getAllUsers() {
     var users =    userRepository.findAll();
     if(users.isEmpty()) {
         return null;
     }
        return users;
    }

    @Override
    public User getUserByUserName(String name) {
       if( name.isEmpty()) {
           return null;
       }

       var user = userRepository.findUserByUsername(name);
        System.out.println("IN USER SERVICE: " + user);
         if(user == null) {
             return null;
         }
        return user;
    }

    @Override
    @Transactional
    public void CreateUser(User user) {
    /*    if (user == null) {
            throw new IllegalArgumentException("User object cannot be null");
        }


        if (userRepository.findUserByUsername(user.getUsername()) != null) {
            System.out.println("Attempted to create a user with existing username: {}"+ user.getUsername());
            throw new IllegalStateException("Username already exists");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        System.out.println("User created: {}" + user.getUsername());*/

        user.setId(null); // just in case it's set from outside
        user.setPassword(passwordEncoder.encode(user.getPassword()));
         userRepository.save(user); // should insert, not update
    }

    @Override
    public User updateUser(Integer id, User newUserData) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserException("User not found"));

        existingUser.setFirstName(newUserData.getFirstName());
        existingUser.setLastName(newUserData.getLastName());
        existingUser.setEmail(newUserData.getEmail());
        existingUser.setAvatar(newUserData.getAvatar());

        existingUser.setWins(newUserData.getWins());
        existingUser.setGames(newUserData.getGames());

        if (newUserData.getPassword() != null && !newUserData.getPassword().isBlank()) {
            existingUser.setPassword(passwordEncoder.encode(newUserData.getPassword()));
        }

        return userRepository.save(existingUser);
    }

    @Override
    public void deleteUser(String username, String password) {
        User user = userRepository.findUserByUsername(username);

        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            throw new SecurityException("Invalid credentials");
        }
        userRepository.delete(user);
    }

    @Override
    public User getUserById(Long id) {

        if(id == null) {
            return null;
        }

       User user =  userRepository.findUserById(id);
        if(user == null) {
            throw new NoSuchElementException("User not found");
        }
        return user;
    }

    @Override
    public Integer getUserWins(Long id) {
      Integer wins =  userRepository.getUserWins(id);
      if(wins == null) {
          throw new NoSuchElementException("NO WINS FOUND FOR USER WITH ID: " + id);
      }
        return wins;
    }

    @Override
    public Integer getUserGamesPlayed(Long id) {

        Integer gamesPlayed = userRepository.getUserGamesPlayed(id);
        System.out.println("Games played for user with ID " + id + ": " + gamesPlayed);
        if(gamesPlayed == null) {
            throw new NoSuchElementException("NO GAMES FOUND FOR USER WITH ID: " + id);
        }
        return gamesPlayed;
    }

    @Override
    @Transactional
    public void incrementGamesPlayed(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }

        int updatedRows = userRepository.incrementGamesPlayed(userId);

        if (updatedRows == 0) {
            throw new NoSuchElementException("No user found with ID: " + userId);
        }

        System.out.println("Incremented games played for user ID: " + userId);
    }
}

package com.proyecto3.risk.service;

import com.proyecto3.risk.model.dtos.UpdateUserDto;
import com.proyecto3.risk.model.entities.User;

import java.util.List;

public interface UserService {


     List<User> getAllUsers();
     User getUserByUserName(String name);
     void CreateUser(User user);
     User updateUser(Integer id, User newUserData);
     void deleteUser(String username, String password);
     User getUserById(Long id);
     Integer getUserWins(Long id);
     Integer getUserGamesPlayed(Long id);

}

package com.proyecto3.risk.repository;

import com.proyecto3.risk.model.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository <User,Integer> {


    @Query("SELECT u FROM User u WHERE LOWER(u.username) = LOWER(?1)")
   public User findUserByUsername(String username);

    @Query("SELECT u FROM User u WHERE u.id = ?1")
    public User findUserById(Long id);

    @Query("SELECT u.wins FROM User u WHERE u.id = ?1")
    public Integer getUserWins(Long id);

    @Query("SELECT u.games FROM User u WHERE u.id = ?1")
    public Integer getUserGamesPlayed(Long id);
}

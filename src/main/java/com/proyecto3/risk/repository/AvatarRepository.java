package com.proyecto3.risk.repository;

import com.proyecto3.risk.model.Avatars;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AvatarRepository extends JpaRepository<Avatars, Integer> {

    @Query("SELECT a FROM Avatars a WHERE a.name = ?1")
    Avatars findByName(String name);
}

package com.proyecto3.risk.repository;

import com.proyecto3.risk.model.entities.Avatars;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AvatarRepository extends JpaRepository<Avatars, Integer> {

    @Query("SELECT a FROM Avatars a WHERE a.name = ?1")
    Avatars findByName(String name);
}

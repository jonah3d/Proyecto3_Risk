package com.proyecto3.risk.model.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "partida", indexes = {
        @Index(name = "turn_state", columnList = "turn_state")
})
public class Partida {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "date", nullable = false)
    private Instant date;

    @Size(max = 255)
    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Size(max = 255)
    @NotNull
    @Column(name = "token", nullable = false)
    private String token;

    @NotNull
    @Column(name = "max_players", nullable = false)
    private Integer maxPlayers;

    @Column(name = "admin_id")
    private Long adminId;

    @Column(name = "turn_player_id")
    private Long turnPlayerId;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "turn_state", nullable = false)
    private State turnState;


    @NotNull
    @Column(name = "is_public",nullable = false)
    private int IsPublic;

    @OneToMany(mappedBy = "partida")
    private Set<Player> players = new LinkedHashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getDate() {
        return date;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Integer getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(Integer maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public Long getAdminId() {
        return adminId;
    }

    public void setAdminId(Long adminId) {
        this.adminId = adminId;
    }

    public Long getTurnPlayerId() {
        return turnPlayerId;
    }

    public void setTurnPlayerId(Long turnPlayerId) {
        this.turnPlayerId = turnPlayerId;
    }

    public State getTurnState() {
        return turnState;
    }

    public void setTurnState(State turnState) {
        this.turnState = turnState;
    }

    public Set<Player> getPlayers() {
        return players;
    }

    public void setPlayers(Set<Player> players) {
        this.players = players;
    }

    public int getIsPublic() {
        return IsPublic;
    }

    public void setIsPublic(int isPublic) {
        IsPublic = isPublic;
    }
}
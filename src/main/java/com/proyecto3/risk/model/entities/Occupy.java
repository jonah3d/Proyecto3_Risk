package com.proyecto3.risk.model.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@IdClass(OccupyId.class)
@Entity
@Table(name = "occupy", indexes = {
        @Index(name = "player_id", columnList = "player_id")
})
public class Occupy {
    @Id
    @NotNull
    @Column(name = "country_id", nullable = false)
    private Long countryId;

    @Id
    @NotNull
    @Column(name = "player_id", nullable = false)
    private Long playerId;

    @MapsId
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "country_id", nullable = false)
    private Country country;

    @MapsId
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;

    @NotNull
    @Column(name = "troops", nullable = false)
    private Integer troops;

    public Occupy() {
    }

    public Occupy(Long playerId, Long countryId, Integer troops) {
        this.countryId = countryId;
        this.playerId = playerId;
        this.troops = troops;
    }

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }

    public Long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Long playerId) {
        this.playerId = playerId;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Integer getTroops() {
        return troops;
    }

    public void setTroops(Integer troops) {
        this.troops = troops;
    }

}
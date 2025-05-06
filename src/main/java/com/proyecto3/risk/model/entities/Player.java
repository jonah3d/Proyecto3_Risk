package com.proyecto3.risk.model.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "player", uniqueConstraints = {
        @UniqueConstraint(name = "user_id", columnNames = {"user_id", "partida_id"}),
        @UniqueConstraint(name = "partida_id", columnNames = {"partida_id", "skf_numero"})
})
public class Player {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "partida_id", nullable = false)
    private Partida partida;

    @NotNull
    @Column(name = "skf_numero", nullable = false)
    private Integer skfNumero;

    @OneToMany(mappedBy = "player")
    private Set<Occupy> occupies = new LinkedHashSet<>();

    @OneToMany(mappedBy = "player")
    private List<Ma> cards; // or maList

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Partida getPartida() {
        return partida;
    }

    public void setPartida(Partida partida) {
        this.partida = partida;
    }

    public Integer getSkfNumero() {
        return skfNumero;
    }

    public void setSkfNumero(Integer skfNumero) {
        this.skfNumero = skfNumero;
    }

    public Set<Occupy> getOccupies() {
        return occupies;
    }

    public void setOccupies(Set<Occupy> occupies) {
        this.occupies = occupies;
    }

}